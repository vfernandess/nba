package com.voidx.nba.feature.game

import androidx.lifecycle.SavedStateHandle
import com.voidx.nba.core.ui.ContentState
import com.voidx.nba.domain.model.Page
import com.voidx.nba.feature.game.business.CurrentSeason
import com.voidx.nba.feature.game.business.GameViewModel
import com.voidx.nba.feature.game.business.GetTeamGamesUseCase
import com.voidx.nba.feature.game.business.Today
import com.voidx.nba.feature.game.presentation.GameCursor
import com.voidx.nba.feature.game.presentation.GameInteractor
import com.voidx.nba.feature.game.presentation.GameReducer
import com.voidx.nba.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    @get:Rule
    val rule = MainDispatcherRule()

    private val games = listOf(game(1), game(2))

    private fun viewModel(handle: SavedStateHandle, repository: FakeGameRepository) = GameViewModel(
        savedStateHandle = handle,
        reducer = GameReducer.Impl(),
        interactor = GameInteractor.Impl(
            getTeamGamesUseCase = GetTeamGamesUseCase.Impl(repository, Today { LocalDate.of(2026, 11, 16) }),
            currentSeason = object : CurrentSeason {
                override fun invoke(): Int = 2026
            },
        ),
        runContext = rule.dispatcher,
    )

    @Test
    fun init_readsTeamFromSavedStateAndLoadsGames() = runTest(rule.dispatcher) {
        val repo = FakeGameRepository(mapOf((2026 to null) to Page(games, null)))
        val handle = SavedStateHandle(
            mapOf("teamId" to 14, "teamName" to "Atlanta Hawks"),
        )

        val vm = viewModel(handle, repo)
        advanceUntilIdle()

        val state = vm.state.value
        assertEquals(14, state.teamId)
        assertEquals("Atlanta Hawks", state.teamName)
        assertEquals(ContentState.Success(games), state.content)
        assertEquals(GameCursor(2025), state.next)
        assertEquals(14, repo.calls.single().teamId)
    }
}
