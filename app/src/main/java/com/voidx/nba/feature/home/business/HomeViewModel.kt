package com.voidx.nba.feature.home.business

import com.voidx.nba.core.fuse.SideEffectViewModel
import com.voidx.nba.feature.home.presentation.HomeCommand
import com.voidx.nba.feature.home.presentation.HomeEffect
import com.voidx.nba.feature.home.presentation.HomeInteractor
import com.voidx.nba.feature.home.presentation.HomeReducer
import com.voidx.nba.feature.home.presentation.HomeSideEffect
import com.voidx.nba.feature.home.presentation.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class HomeViewModel @Inject constructor(
    runContext: CoroutineContext,
    interactor: HomeInteractor,
    reducer: HomeReducer,
) : SideEffectViewModel<HomeCommand, HomeEffect, HomeReducer, HomeState, HomeSideEffect>(
    runContext,
    interactor,
    reducer,
) {

    init {
        dispatch(HomeCommand.Load)
    }
}
