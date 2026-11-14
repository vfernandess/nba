package com.voidx.nba.feature.player.presentation

import com.voidx.nba.core.fuse.SideEffectInteractor
import com.voidx.nba.feature.player.business.SearchPlayersUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

abstract class PlayerInteractor :
    SideEffectInteractor<PlayerState, PlayerCommand, PlayerEffect, PlayerSideEffect>() {

    class Impl @Inject constructor(
        private val searchPlayers: SearchPlayersUseCase,
    ) : PlayerInteractor() {

        override suspend fun invoke(state: PlayerState, command: PlayerCommand): Flow<PlayerEffect> =
            when (command) {
                is PlayerCommand.Search -> search(command.query)
                PlayerCommand.Retry -> searchNow(state.query)
                PlayerCommand.LoadMore -> loadMore(state)
                is PlayerCommand.SelectPlayer -> selectPlayer(command)
            }

        // Cancelling this flow (a newer Search/LoadMore/Retry shares SEARCH_JOB_KEY) is the debounce.
        private fun search(rawQuery: String): Flow<PlayerEffect> = flow {
            emit(PlayerEffect.QueryChanged(rawQuery))
            val query = rawQuery.trim()
            if (query.length < MIN_QUERY_LENGTH) {
                emit(PlayerEffect.ShowPrompt)
                return@flow
            }
            delay(SEARCH_DEBOUNCE_MS)
            emitAll(searchPlayers(query, cursor = null))
        }

        private fun searchNow(rawQuery: String): Flow<PlayerEffect> {
            val query = rawQuery.trim()
            return if (query.length < MIN_QUERY_LENGTH) {
                flowOf(PlayerEffect.ShowPrompt)
            } else {
                searchPlayers(query, cursor = null)
            }
        }

        private fun loadMore(state: PlayerState): Flow<PlayerEffect> {
            val cursor = state.nextCursor
            return if (cursor == null || !state.canLoadMore()) {
                emptyFlow()
            } else {
                searchPlayers(state.resultsQuery, cursor)
            }
        }

        private fun selectPlayer(command: PlayerCommand.SelectPlayer): Flow<PlayerEffect> {
            val team = command.player.team
            return flowOf(
                PlayerEffect.PlayerSelected(command.player) sendSideEffect
                    PlayerSideEffect.OpenTeam(team.id, team.fullName),
            )
        }
    }
}
