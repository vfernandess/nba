package com.voidx.nba.feature.player

import com.voidx.nba.core.ui.ContentState
import com.voidx.nba.domain.model.Page
import com.voidx.nba.feature.player.business.PlayerViewModel
import com.voidx.nba.feature.player.business.SearchPlayersUseCase
import com.voidx.nba.feature.player.presentation.PlayerCommand
import com.voidx.nba.feature.player.presentation.PlayerEffect
import com.voidx.nba.feature.player.presentation.PlayerInteractor
import com.voidx.nba.feature.player.presentation.PlayerReducer
import com.voidx.nba.feature.player.presentation.SEARCH_JOB_KEY
import com.voidx.nba.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModelTest {

    @get:Rule
    val rule = MainDispatcherRule()

    private val repository = FakePlayerRepository()

    private fun viewModel(reducer: PlayerReducer) = PlayerViewModel(
        reducer = reducer,
        interactor = PlayerInteractor.Impl(SearchPlayersUseCase.Impl(repository)),
        runContext = rule.dispatcher,
    )

    @Test
    fun loadMore_isCancelledByNewSearch() = runTest(rule.dispatcher) {
        val lePage1 = listOf(testPlayer(1, "Le", "One"))
        val lePage2 = listOf(testPlayer(2, "Le", "Two"))
        val lebron = listOf(testPlayer(3, "LeBron", "James"))
        repository.handler = { query, cursor, _ ->
            if (cursor != null) {
                delay(1_000)
                Page(lePage2, nextCursor = null)
            } else {
                check(query == "lebron")
                Page(lebron, nextCursor = null)
            }
        }
        val reducer = PlayerReducer.Impl()
        reducer(PlayerEffect.QueryChanged("le"))
        reducer(PlayerEffect.ShowPage(lePage1, nextCursor = 25, append = false))
        val vm = viewModel(reducer)

        vm.dispatch(PlayerCommand.LoadMore, SEARCH_JOB_KEY)
        runCurrent()
        assertEquals(true, vm.state.value.isLoadingMore)

        vm.dispatch(PlayerCommand.Search("lebron"), SEARCH_JOB_KEY)
        advanceUntilIdle()

        val state = vm.state.value
        assertEquals(ContentState.Success(lebron), state.content)
        assertEquals("lebron", state.query)
        assertFalse(state.isLoadingMore)
        assertEquals(listOf(25 to "le", null to "lebron"), repository.calls.map { it.cursor to it.query })
    }

    @Test
    fun rapidSearches_onlyLastOneHitsRepository() = runTest(rule.dispatcher) {
        repository.handler = { _, _, _ -> Page(listOf(testPlayer(1)), nextCursor = null) }
        val vm = viewModel(PlayerReducer.Impl())

        vm.dispatch(PlayerCommand.Search("le"), SEARCH_JOB_KEY)
        runCurrent()
        vm.dispatch(PlayerCommand.Search("leb"), SEARCH_JOB_KEY)
        runCurrent()
        vm.dispatch(PlayerCommand.Search("lebr"), SEARCH_JOB_KEY)
        advanceUntilIdle()

        assertEquals(listOf("lebr"), repository.calls.map { it.query })
    }
}
