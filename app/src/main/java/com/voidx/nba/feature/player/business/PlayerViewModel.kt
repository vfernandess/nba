package com.voidx.nba.feature.player.business

import com.voidx.nba.core.fuse.SideEffectViewModel
import com.voidx.nba.feature.player.presentation.PlayerCommand
import com.voidx.nba.feature.player.presentation.PlayerEffect
import com.voidx.nba.feature.player.presentation.PlayerInteractor
import com.voidx.nba.feature.player.presentation.PlayerReducer
import com.voidx.nba.feature.player.presentation.PlayerSideEffect
import com.voidx.nba.feature.player.presentation.PlayerState
import com.voidx.nba.feature.player.presentation.SEARCH_JOB_KEY
import com.voidx.nba.feature.player.presentation.canLoadMore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class PlayerViewModel @Inject constructor(
    reducer: PlayerReducer,
    interactor: PlayerInteractor,
    runContext: CoroutineContext,
) : SideEffectViewModel<PlayerCommand, PlayerEffect, PlayerReducer, PlayerState, PlayerSideEffect>(
    runContext,
    interactor,
    reducer,
) {

    /**
     * Gated before dispatch: a keyed dispatch cancels the in-flight job, so a LoadMore that
     * cannot run must never reach [dispatch].
     */
    fun onReachedEnd() {
        if (state.value.canLoadMore()) {
            dispatch(PlayerCommand.LoadMore, SEARCH_JOB_KEY)
        }
    }
}
