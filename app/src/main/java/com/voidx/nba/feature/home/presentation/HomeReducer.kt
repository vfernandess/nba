package com.voidx.nba.feature.home.presentation

import com.voidx.nba.core.fuse.SimpleReducer
import com.voidx.nba.core.ui.ContentState
import com.voidx.nba.core.ui.EmptyReason
import javax.inject.Inject

abstract class HomeReducer : SimpleReducer<HomeState, HomeEffect>(HomeState()) {

    class Impl @Inject constructor() : HomeReducer() {

        override fun reduce(effect: HomeEffect, currentState: HomeState): HomeState {
            return when (effect) {
                is HomeEffect.Loading ->
                    currentState.copy(content = ContentState.Loading)

                is HomeEffect.ShowTeams ->
                    currentState.copy(content = ContentState.Success(effect.teams))

                is HomeEffect.ShowEmpty ->
                    currentState.copy(content = ContentState.Empty(EmptyReason.NoData))

                is HomeEffect.ShowError ->
                    currentState.copy(content = ContentState.Error(effect.error))

                is HomeEffect.SortChanged ->
                    currentState.copy(
                        sort = effect.sort,
                        content = effect.teams?.let { ContentState.Success(it) } ?: currentState.content,
                    )
            }
        }
    }
}
