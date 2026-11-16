package com.voidx.nba.feature.game

import com.voidx.nba.core.common.AppError
import com.voidx.nba.core.common.AppException
import com.voidx.nba.domain.model.Page
import com.voidx.nba.feature.game.business.GetTeamGamesUseCase
import com.voidx.nba.feature.game.business.MAX_PAGES_PER_SEASON
import com.voidx.nba.feature.game.business.Today
import com.voidx.nba.feature.game.presentation.GameCursor
import com.voidx.nba.feature.game.presentation.GameEffect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class GetTeamGamesUseCaseTest {

    private val today = LocalDate.of(2026, 11, 16)
    private val october = game(1, date = LocalDate.of(2026, 10, 22))
    private val november = game(2, date = LocalDate.of(2026, 11, 14))
    private val games = listOf(october, november)
    private val newestFirst = listOf(november, october)

    private fun useCase(repository: FakeGameRepository) =
        GetTeamGamesUseCase.Impl(repository, Today { today })

    @Test
    fun season_isShownNewestFirst() = runTest {
        val repo = FakeGameRepository(mapOf((2026 to null) to Page(games, null)))

        val effects = useCase(repo)(14, GameCursor(2026), append = false).toList()

        assertEquals(
            listOf(GameEffect.Loading, GameEffect.ShowPage(newestFirst, GameCursor(2025), false)),
            effects,
        )
    }

    @Test
    fun request_usesPerPage100AndStopsAtToday() = runTest {
        val repo = FakeGameRepository(mapOf((2026 to null) to Page(games, null)))

        useCase(repo)(14, GameCursor(2026), append = false).toList()

        assertEquals(listOf(FakeGameRepository.Call(14, 2026, null, 100, today)), repo.calls)
    }

    @Test
    fun seasonSpreadOverPages_isMergedThenSortedNewestFirst() = runTest {
        // e.g. a season with playoffs: 100 games on page one, the rest behind the cursor.
        val playoff = game(3, date = LocalDate.of(2026, 11, 15))
        val repo = FakeGameRepository(
            mapOf(
                (2026 to null) to Page(games, 9),
                (2026 to 9) to Page(listOf(playoff), null),
            ),
        )

        val effects = useCase(repo)(14, GameCursor(2026), append = false).toList()

        assertEquals(GameEffect.ShowPage(listOf(playoff) + newestFirst, GameCursor(2025), false), effects.last())
        assertEquals(listOf(2026 to null, 2026 to 9), repo.calls.map { it.season to it.cursor })
    }

    @Test
    fun secondPageFails_showsErrorAndNoPartialSeason() = runTest {
        val repo = FakeGameRepository(
            pages = mapOf((2026 to null) to Page(games, 9)),
            failure = AppException(AppError.RateLimited),
            failOn = 2026 to 9,
        )

        val effects = useCase(repo)(14, GameCursor(2026), append = false).toList()

        assertEquals(listOf(GameEffect.Loading, GameEffect.ShowError(AppError.RateLimited)), effects)
        assertEquals(listOf(today, today), repo.calls.map { it.endDate })
    }

    @Test
    fun overlappingPages_areDeduplicated() = runTest {
        val repo = FakeGameRepository(
            mapOf(
                (2026 to null) to Page(games, 9),
                (2026 to 9) to Page(listOf(november), null),
            ),
        )

        val effects = useCase(repo)(14, GameCursor(2026), append = false).toList()

        assertEquals(GameEffect.ShowPage(newestFirst, GameCursor(2025), false), effects.last())
    }

    @Test
    fun cursorThatDoesNotAdvance_stopsPaging() = runTest {
        val repo = FakeGameRepository(
            mapOf(
                (2026 to null) to Page(games, 9),
                (2026 to 9) to Page(listOf(game(3, date = LocalDate.of(2026, 11, 15))), 9),
            ),
        )

        useCase(repo)(14, GameCursor(2026), append = false).toList()

        assertEquals(listOf(2026 to null, 2026 to 9), repo.calls.map { it.season to it.cursor })
    }

    @Test
    fun endlessCursor_isCappedAtMaxPagesPerSeason() = runTest {
        val repo = FakeGameRepository(
            (0..5).associate { page ->
                (2026 to (if (page == 0) null else page)) to Page(listOf(game(10 + page)), page + 1)
            },
        )

        useCase(repo)(14, GameCursor(2026), append = false).toList()

        assertEquals(MAX_PAGES_PER_SEASON, repo.calls.size)
    }

    @Test
    fun emptyCurrentSeason_walksBackToPreviousSeasonInOneEmission() = runTest {
        val repo = FakeGameRepository(mapOf((2025 to null) to Page(games, null)))

        val effects = useCase(repo)(14, GameCursor(2026), append = false).toList()

        assertEquals(
            listOf(GameEffect.Loading, GameEffect.ShowPage(newestFirst, GameCursor(2024), false)),
            effects,
        )
        assertEquals(listOf(2026, 2025), repo.calls.map { it.season })
    }

    @Test
    fun threeEmptySeasonsInARow_emitsEmptyAfterExactlyThreeCalls() = runTest {
        val repo = FakeGameRepository()

        val effects = useCase(repo)(14, GameCursor(2026), append = false).toList()

        assertEquals(listOf(GameEffect.Loading, GameEffect.ShowEmpty), effects)
        assertEquals(3, repo.calls.size)
    }

    @Test
    fun threeEmptySeasonsInARow_onAppend_emitsEmptyAppendedPageWithNoNext() = runTest {
        val repo = FakeGameRepository()

        val effects = useCase(repo)(14, GameCursor(2025), append = true).toList()

        assertEquals(
            listOf(GameEffect.LoadingMore, GameEffect.ShowPage(emptyList(), null, true)),
            effects,
        )
        assertEquals(3, repo.calls.size)
    }

    @Test
    fun firstSeason_nextIsNull() = runTest {
        val repo = FakeGameRepository(mapOf((1946 to null) to Page(games, null)))

        val effects = useCase(repo)(14, GameCursor(1946), append = false).toList()

        assertEquals(GameEffect.ShowPage(newestFirst, null, false), effects.last())
    }

    @Test
    fun appendWithError_emitsLoadingMoreThenLoadMoreError() = runTest {
        val repo = FakeGameRepository(failure = AppException(AppError.RateLimited))

        val effects = useCase(repo)(14, GameCursor(2025), append = true).toList()

        assertEquals(
            listOf(GameEffect.LoadingMore, GameEffect.ShowLoadMoreError(AppError.RateLimited)),
            effects,
        )
    }

    @Test
    fun firstLoadWithError_emitsLoadingThenError() = runTest {
        val repo = FakeGameRepository(failure = AppException(AppError.Network))

        val effects = useCase(repo)(14, GameCursor(2026), append = false).toList()

        assertEquals(listOf(GameEffect.Loading, GameEffect.ShowError(AppError.Network)), effects)
    }
}
