package com.voidx.nba.feature.game.presentation

import com.voidx.nba.core.fuse.SimpleReducer
import com.voidx.nba.core.ui.ContentState
import com.voidx.nba.core.ui.EmptyReason
import javax.inject.Inject

abstract class GameReducer : SimpleReducer<GameState, GameEffect>(GameState()) {

    class Impl @Inject constructor() : GameReducer() {

        override fun reduce(effect: GameEffect, currentState: GameState): GameState =
            when (effect) {
                is GameEffect.Started -> currentState.copy(
                    teamId = effect.teamId,
                    teamName = effect.teamName,
                    origin = effect.origin,
                )

                GameEffect.Loading -> currentState.copy(
                    content = ContentState.Loading,
                    next = null,
                    isLoadingMore = false,
                    loadMoreError = null,
                )

                GameEffect.LoadingMore -> currentState.copy(
                    isLoadingMore = true,
                    loadMoreError = null,
                )

                is GameEffect.ShowPage -> currentState.copy(
                    content = ContentState.Success(pageGames(currentState, effect)),
                    next = effect.next,
                    isLoadingMore = false,
                    loadMoreError = null,
                )

                GameEffect.ShowEmpty -> currentState.copy(
                    content = ContentState.Empty(EmptyReason.NoData),
                    next = null,
                    isLoadingMore = false,
                )

                is GameEffect.ShowError -> currentState.copy(
                    content = ContentState.Error(effect.error),
                    next = null,
                    isLoadingMore = false,
                )

                is GameEffect.ShowLoadMoreError -> currentState.copy(
                    isLoadingMore = false,
                    loadMoreError = effect.error,
                )
            }

        private fun pageGames(state: GameState, effect: GameEffect.ShowPage) =
            if (effect.append) {
                // distinctBy: a duplicated page must never crash the list's `key = { it.id }`.
                ((state.content as? ContentState.Success)?.data.orEmpty() + effect.games).distinctBy { it.id }
            } else {
                effect.games
            }
    }
}
