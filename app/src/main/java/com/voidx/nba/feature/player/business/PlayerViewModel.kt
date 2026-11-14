package com.voidx.nba.feature.player.business

import com.voidx.nba.core.fuse.SideEffectViewModel
import com.voidx.nba.feature.player.presentation.PlayerCommand
import com.voidx.nba.feature.player.presentation.PlayerEffect
import com.voidx.nba.feature.player.presentation.PlayerInteractor
import com.voidx.nba.feature.player.presentation.PlayerReducer
import com.voidx.nba.feature.player.presentation.PlayerSideEffect
import com.voidx.nba.feature.player.presentation.PlayerState
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
)
