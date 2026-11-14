package com.voidx.nba.feature.game.presentation

import com.voidx.nba.core.fuse.Interactor
import com.voidx.nba.feature.game.business.CurrentSeason
import com.voidx.nba.feature.game.business.GetTeamGamesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface GameInteractor : Interactor<GameState, GameCommand, GameEffect> {

    class Impl @Inject constructor(
        private val getTeamGamesUseCase: GetTeamGamesUseCase,
        private val currentSeason: CurrentSeason,
    ) : GameInteractor {

        override suspend fun invoke(state: GameState, command: GameCommand): Flow<GameEffect> =
            when (command) {
                is GameCommand.Start -> flow {
                    emit(GameEffect.Started(command.teamId, command.teamName, command.origin))
                    emitAll(firstPage(command.teamId))
                }

                GameCommand.Retry -> firstPage(state.teamId)

                GameCommand.LoadMore -> loadMore(state)
            }

        private fun firstPage(teamId: Int): Flow<GameEffect> =
            getTeamGamesUseCase(teamId, GameCursor(currentSeason()), append = false)

        private fun loadMore(state: GameState): Flow<GameEffect> {
            val next = state.next
            return if (state.isLoadingMore || next == null) {
                emptyFlow()
            } else {
                getTeamGamesUseCase(state.teamId, next, append = true)
            }
        }
    }
}
