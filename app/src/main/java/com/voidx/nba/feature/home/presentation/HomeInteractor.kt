package com.voidx.nba.feature.home.presentation

import com.voidx.nba.core.fuse.SideEffectInteractor
import com.voidx.nba.core.ui.ContentState
import com.voidx.nba.feature.home.business.GetTeamsUseCase
import com.voidx.nba.feature.home.business.SortTeamsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

abstract class HomeInteractor :
    SideEffectInteractor<HomeState, HomeCommand, HomeEffect, HomeSideEffect>() {

    class Impl @Inject constructor(
        private val getTeamsUseCase: GetTeamsUseCase,
        private val sortTeamsUseCase: SortTeamsUseCase,
    ) : HomeInteractor() {

        override suspend fun invoke(state: HomeState, command: HomeCommand): Flow<HomeEffect> {
            return when (command) {
                is HomeCommand.Load ->
                    if (state.content is ContentState.Success) emptyFlow() else getTeamsUseCase(state.sort)

                is HomeCommand.Retry ->
                    getTeamsUseCase(state.sort)

                is HomeCommand.SelectSort ->
                    if (state.content is ContentState.Loading) {
                        emptyFlow()
                    } else {
                        flowOf(HomeEffect.SortChanged(command.sort, state.sortedTeams(command)))
                    }

                is HomeCommand.SelectTeam ->
                    flowOf(
                        HomeEffect.TeamSelected(command.team) sendSideEffect
                            HomeSideEffect.OpenTeam(command.team.id, command.team.fullName),
                    )
            }
        }

        private fun HomeState.sortedTeams(command: HomeCommand.SelectSort) =
            (content as? ContentState.Success)?.data?.let { sortTeamsUseCase(it, command.sort) }
    }
}
