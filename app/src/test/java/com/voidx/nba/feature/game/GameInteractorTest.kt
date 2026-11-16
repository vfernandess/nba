package com.voidx.nba.feature.game

import com.voidx.nba.core.common.AppError
import com.voidx.nba.feature.game.business.CurrentSeason
import com.voidx.nba.feature.game.business.GetTeamGamesUseCase
import com.voidx.nba.feature.game.presentation.GameCommand
import com.voidx.nba.feature.game.presentation.GameCursor
import com.voidx.nba.feature.game.presentation.GameEffect
import com.voidx.nba.feature.game.presentation.GameInteractor
import com.voidx.nba.feature.game.presentation.GameState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GameInteractorTest {

    private class FixedSeason(private val season: Int = 2026) : CurrentSeason {
        override fun invoke(): Int = season
    }

    private class RecordingUseCase : GetTeamGamesUseCase {
        data class Call(val teamId: Int, val from: GameCursor, val append: Boolean)

        val calls = mutableListOf<Call>()

        override fun invoke(teamId: Int, from: GameCursor, append: Boolean): Flow<GameEffect> {
            calls += Call(teamId, from, append)
            return flowOf(GameEffect.Loading)
        }
    }

    private val useCase = RecordingUseCase()
    private val interactor = GameInteractor.Impl(useCase, FixedSeason())
    private val baseState = GameState(teamId = 14, teamName = "Atlanta Hawks")

    @Test
    fun start_emitsStartedThenLoadsFromCurrentSeason() = runTest {
        val effects = interactor(GameState(), GameCommand.Start(14, "Atlanta Hawks")).toList()

        assertEquals(
            listOf(GameEffect.Started(14, "Atlanta Hawks"), GameEffect.Loading),
            effects,
        )
        assertEquals(listOf(RecordingUseCase.Call(14, GameCursor(2026), false)), useCase.calls)
    }

    @Test
    fun retry_restartsFromCurrentSeasonWithoutAppend() = runTest {
        interactor(baseState.copy(next = GameCursor(2020)), GameCommand.Retry).toList()

        assertEquals(listOf(RecordingUseCase.Call(14, GameCursor(2026), false)), useCase.calls)
    }

    @Test
    fun loadMore_usesStoredCursorWithAppend() = runTest {
        val effects = interactor(baseState.copy(next = GameCursor(2025)), GameCommand.LoadMore).toList()

        assertEquals(listOf(GameEffect.Loading), effects)
        assertEquals(listOf(RecordingUseCase.Call(14, GameCursor(2025), true)), useCase.calls)
    }

    @Test
    fun loadMore_ignoredWhileLoadingMore() = runTest {
        val state = baseState.copy(next = GameCursor(2025), isLoadingMore = true)

        val effects = interactor(state, GameCommand.LoadMore).toList()

        assertEquals(emptyList<GameEffect>(), effects)
        assertEquals(0, useCase.calls.size)
    }

    @Test
    fun loadMore_ignoredWhenThereIsNoNextCursor() = runTest {
        val effects = interactor(baseState.copy(next = null), GameCommand.LoadMore).toList()

        assertEquals(emptyList<GameEffect>(), effects)
        assertEquals(0, useCase.calls.size)
    }

    @Test
    fun loadMore_afterLoadMoreError_isAllowedAgain() = runTest {
        val state = baseState.copy(next = GameCursor(2025), loadMoreError = AppError.Network)

        interactor(state, GameCommand.LoadMore).toList()

        assertEquals(1, useCase.calls.size)
    }
}
