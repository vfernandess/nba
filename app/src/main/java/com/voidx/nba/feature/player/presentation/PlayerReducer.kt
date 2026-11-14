package com.voidx.nba.feature.player.presentation

import com.voidx.nba.core.fuse.SimpleReducer
import com.voidx.nba.core.ui.ContentState
import com.voidx.nba.core.ui.EmptyReason
import com.voidx.nba.domain.model.Player
import javax.inject.Inject

abstract class PlayerReducer : SimpleReducer<PlayerState, PlayerEffect>(PlayerState()) {

    class Impl @Inject constructor() : PlayerReducer() {

        override fun reduce(effect: PlayerEffect, currentState: PlayerState): PlayerState =
            when (effect) {
                is PlayerEffect.QueryChanged -> currentState.copy(query = effect.query)

                PlayerEffect.ShowPrompt -> currentState.firstPageOf(ContentState.Empty(EmptyReason.Prompt))

                PlayerEffect.Loading -> currentState.firstPageOf(ContentState.Loading)

                is PlayerEffect.ShowNoResults ->
                    currentState.firstPageOf(ContentState.Empty(EmptyReason.NoResults(effect.query)))

                is PlayerEffect.ShowError -> currentState.firstPageOf(ContentState.Error(effect.error))

                PlayerEffect.LoadingMore -> currentState.copy(isLoadingMore = true, loadMoreError = null)

                is PlayerEffect.ShowPage -> {
                    val current = (currentState.content as? ContentState.Success)?.data
                    val players = if (effect.append && current != null) current + effect.players else effect.players
                    currentState.copy(
                        content = ContentState.Success(players),
                        nextCursor = effect.nextCursor,
                        isLoadingMore = false,
                        loadMoreError = null,
                    )
                }

                is PlayerEffect.ShowLoadMoreError ->
                    currentState.copy(isLoadingMore = false, loadMoreError = effect.error)
            }

        private fun PlayerState.firstPageOf(content: ContentState<List<Player>>) =
            copy(content = content, nextCursor = null, isLoadingMore = false, loadMoreError = null)
    }
}
