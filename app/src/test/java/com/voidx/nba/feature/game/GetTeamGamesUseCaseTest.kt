package com.voidx.nba.feature.game

import com.voidx.nba.core.common.AppError
import com.voidx.nba.core.common.AppException
import com.voidx.nba.domain.model.Page
import com.voidx.nba.feature.game.business.GetTeamGamesUseCase
import com.voidx.nba.feature.game.presentation.GameCursor
import com.voidx.nba.feature.game.presentation.GameEffect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetTeamGamesUseCaseTest {

    private val games = listOf(game(1), game(2))

    private fun useCase(repository: FakeGameRepository) = GetTeamGamesUseCase.Impl(repository)

    @Test
    fun firstPage_withNextCursor_emitsLoadingThenPageAndUsesPerPage100() = runTest {
        val repo = FakeGameRepository(mapOf((2026 to null) to Page(games, 9)))

        val effects = useCase(repo)(14, GameCursor(2026), append = false).toList()

        assertEquals(
            listOf(GameEffect.Loading, GameEffect.ShowPage(games, GameCursor(2026, 9), false)),
            effects,
        )
        assertEquals(listOf(FakeGameRepository.Call(14, 2026, null, 100)), repo.calls)
    }

    @Test
    fun seasonExhausted_nextCursorPointsAtPreviousSeason() = runTest {
        val repo = FakeGameRepository(mapOf((2026 to null) to Page(games, null)))

        val effects = useCase(repo)(14, GameCursor(2026), append = false).toList()

        assertEquals(GameEffect.ShowPage(games, GameCursor(2025), false), effects.last())
    }

    @Test
    fun emptyCurrentSeason_walksBackToPreviousSeasonInOneEmission() = runTest {
        val repo = FakeGameRepository(mapOf((2025 to null) to Page(games, null)))

        val effects = useCase(repo)(14, GameCursor(2026), append = false).toList()

        assertEquals(
            listOf(GameEffect.Loading, GameEffect.ShowPage(games, GameCursor(2024), false)),
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
    }

    @Test
    fun firstSeasonExhausted_nextIsNull() = runTest {
        val repo = FakeGameRepository(mapOf((1946 to null) to Page(games, null)))

        val effects = useCase(repo)(14, GameCursor(1946), append = false).toList()

        assertEquals(GameEffect.ShowPage(games, null, false), effects.last())
    }

    @Test
    fun cursorWithEmptyPage_doesNotWalkBackAndMovesToPreviousSeason() = runTest {
        val repo = FakeGameRepository(mapOf((2025 to 30) to Page(emptyList(), null)))

        val effects = useCase(repo)(14, GameCursor(2025, 30), append = true).toList()

        assertEquals(
            listOf(GameEffect.LoadingMore, GameEffect.ShowPage(emptyList(), GameCursor(2024), true)),
            effects,
        )
        assertEquals(1, repo.calls.size)
    }

    @Test
    fun appendWithError_emitsLoadingMoreThenLoadMoreError() = runTest {
        val repo = FakeGameRepository(failure = AppException(AppError.RateLimited))

        val effects = useCase(repo)(14, GameCursor(2025, 30), append = true).toList()

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
