package com.voidx.nba.feature.player

import com.voidx.nba.core.ui.ContentState
import com.voidx.nba.domain.model.Page
import com.voidx.nba.feature.player.business.SearchPlayersUseCase
import com.voidx.nba.feature.player.presentation.MIN_QUERY_LENGTH
import com.voidx.nba.feature.player.presentation.PlayerCommand
import com.voidx.nba.feature.player.presentation.PlayerEffect
import com.voidx.nba.feature.player.presentation.PlayerInteractor
import com.voidx.nba.feature.player.presentation.PlayerSideEffect
import com.voidx.nba.feature.player.presentation.PlayerState
import com.voidx.nba.feature.player.presentation.SEARCH_DEBOUNCE_MS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerInteractorTest {

    private val repository = FakePlayerRepository()
    private val interactor = PlayerInteractor.Impl(SearchPlayersUseCase.Impl(repository))

    private fun TestScope.collect(
        state: PlayerState,
        command: PlayerCommand,
    ): MutableList<PlayerEffect> {
        val effects = mutableListOf<PlayerEffect>()
        launch { interactor(state, command).toList(effects) }
        return effects
    }

    @Test
    fun search_blankOrShortQuery_showsPromptWithoutCallingRepository() = runTest {
        assertEquals(2, MIN_QUERY_LENGTH)
        listOf("  ", " a ", "").forEach { raw ->
            val effects = collect(PlayerState(), PlayerCommand.Search(raw))
            advanceTimeBy(SEARCH_DEBOUNCE_MS * 2)
            runCurrent()

            assertEquals(
                listOf(PlayerEffect.QueryChanged(raw), PlayerEffect.ShowPrompt),
                effects,
            )
        }
        assertEquals(0, repository.calls.size)
    }

    @Test
    fun search_waitsDebounce_thenSearchesWithTrimmedQuery() = runTest {
        val players = listOf(testPlayer(1))
        repository.handler = { _, _, _ -> Page(players, nextCursor = 25) }

        val effects = collect(PlayerState(), PlayerCommand.Search(" lebron "))
        runCurrent()
        assertEquals(listOf<PlayerEffect>(PlayerEffect.QueryChanged(" lebron ")), effects)

        advanceTimeBy(SEARCH_DEBOUNCE_MS - 1)
        runCurrent()
        assertEquals(1, effects.size)
        assertEquals(0, repository.calls.size)

        advanceTimeBy(1)
        runCurrent()
        assertEquals(
            listOf(
                PlayerEffect.QueryChanged(" lebron "),
                PlayerEffect.Loading,
                PlayerEffect.ShowPage(players, 25, append = false),
            ),
            effects,
        )
        assertEquals(listOf(FakePlayerRepository.Call("lebron", null, 25)), repository.calls)
    }

    @Test
    fun retry_searchesImmediatelyWithoutDebounce() = runTest {
        val players = listOf(testPlayer(1))
        repository.handler = { _, _, _ -> Page(players, nextCursor = null) }

        val effects = collect(PlayerState(query = "lebron"), PlayerCommand.Retry)
        runCurrent()

        assertEquals(
            listOf(PlayerEffect.Loading, PlayerEffect.ShowPage(players, null, append = false)),
            effects,
        )
        assertEquals(listOf(FakePlayerRepository.Call("lebron", null, 25)), repository.calls)
    }

    @Test
    fun retry_withShortQuery_showsPrompt() = runTest {
        val effects = collect(PlayerState(query = "l"), PlayerCommand.Retry)
        runCurrent()

        assertEquals(listOf<PlayerEffect>(PlayerEffect.ShowPrompt), effects)
        assertEquals(0, repository.calls.size)
    }

    @Test
    fun loadMore_usesCursorAndResultsQuery() = runTest {
        val more = listOf(testPlayer(3))
        repository.handler = { _, _, _ -> Page(more, nextCursor = null) }
        val state = PlayerState(
            query = " lebron ",
            resultsQuery = "lebron",
            content = ContentState.Success(listOf(testPlayer(1))),
            nextCursor = 25,
        )

        val effects = collect(state, PlayerCommand.LoadMore)
        runCurrent()

        assertEquals(
            listOf(PlayerEffect.LoadingMore, PlayerEffect.ShowPage(more, null, append = true)),
            effects,
        )
        assertEquals(listOf(FakePlayerRepository.Call("lebron", 25, 25)), repository.calls)
    }

    @Test
    fun loadMore_ignoredWhenLoadingOrEnded() = runTest {
        val results = ContentState.Success(listOf(testPlayer(1)))
        val loading = PlayerState(
            query = "lebron", resultsQuery = "lebron", content = results, nextCursor = 25, isLoadingMore = true,
        )
        val ended = PlayerState(query = "lebron", resultsQuery = "lebron", content = results, nextCursor = null)
        val staleQuery = PlayerState(query = "lebron", resultsQuery = "le", content = results, nextCursor = 25)

        val loadingEffects = collect(loading, PlayerCommand.LoadMore)
        val endedEffects = collect(ended, PlayerCommand.LoadMore)
        val staleEffects = collect(staleQuery, PlayerCommand.LoadMore)
        runCurrent()

        assertTrue(loadingEffects.isEmpty())
        assertTrue(endedEffects.isEmpty())
        assertTrue(staleEffects.isEmpty())
        assertEquals(0, repository.calls.size)
    }

    @Test
    fun selectPlayer_sendsOpenTeamSideEffect() = runTest {
        val player = testPlayer(7)

        val effects = collect(PlayerState(query = "le"), PlayerCommand.SelectPlayer(player))
        runCurrent()

        assertEquals(listOf<PlayerEffect>(PlayerEffect.PlayerSelected(player)), effects)
        assertEquals(
            PlayerSideEffect.OpenTeam(player.team.id, player.team.fullName),
            interactor.sideEffect.first(),
        )
    }
}
