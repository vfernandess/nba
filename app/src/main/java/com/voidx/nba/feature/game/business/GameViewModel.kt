package com.voidx.nba.feature.game.business

import androidx.lifecycle.SavedStateHandle
import com.voidx.nba.core.fuse.EffectViewModel
import com.voidx.nba.feature.game.presentation.GameCommand
import com.voidx.nba.feature.game.presentation.GameEffect
import com.voidx.nba.feature.game.presentation.GameInteractor
import com.voidx.nba.feature.game.presentation.GameReducer
import com.voidx.nba.feature.game.presentation.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class GameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    reducer: GameReducer,
    interactor: GameInteractor,
    runContext: CoroutineContext,
) : EffectViewModel<GameCommand, GameEffect, GameReducer, GameState>(
    runContext,
    interactor,
    reducer
) {

    init {
        dispatch(
            GameCommand.Start(
                teamId = checkNotNull(savedStateHandle["teamId"]),
                teamName = checkNotNull(savedStateHandle["teamName"]),
            )
        )
    }
}
