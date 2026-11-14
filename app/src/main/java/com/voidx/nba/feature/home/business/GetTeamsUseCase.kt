package com.voidx.nba.feature.home.business

import com.voidx.nba.core.common.AppError
import com.voidx.nba.core.common.AppException
import com.voidx.nba.domain.model.TeamSort
import com.voidx.nba.domain.repository.TeamRepository
import com.voidx.nba.feature.home.presentation.HomeEffect
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface GetTeamsUseCase {

    operator fun invoke(sort: TeamSort): Flow<HomeEffect>

    class Impl @Inject constructor(
        private val teamRepository: TeamRepository,
        private val sortTeamsUseCase: SortTeamsUseCase,
    ) : GetTeamsUseCase {

        override fun invoke(sort: TeamSort): Flow<HomeEffect> = flow {
            emit(HomeEffect.Loading)
            emit(load(sort))
        }

        private suspend fun load(sort: TeamSort): HomeEffect =
            try {
                val teams = teamRepository.getTeams().filter { it.conference.isNotBlank() }
                if (teams.isEmpty()) {
                    HomeEffect.ShowEmpty
                } else {
                    HomeEffect.ShowTeams(sortTeamsUseCase(teams, sort))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: AppException) {
                HomeEffect.ShowError(e.error)
            } catch (e: Exception) {
                HomeEffect.ShowError(AppError.Unknown)
            }
    }
}
