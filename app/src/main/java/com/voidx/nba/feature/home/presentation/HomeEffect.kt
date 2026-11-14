package com.voidx.nba.feature.home.presentation

import com.voidx.nba.core.common.AppError
import com.voidx.nba.domain.model.Team
import com.voidx.nba.domain.model.TeamSort

sealed class HomeEffect {

    data object Loading : HomeEffect()

    data class ShowTeams(val teams: List<Team>) : HomeEffect()

    data object ShowEmpty : HomeEffect()

    data class ShowError(val error: AppError) : HomeEffect()

    data class SortChanged(val sort: TeamSort, val teams: List<Team>?) : HomeEffect()

    data class TeamSelected(val team: Team) : HomeEffect()
}
