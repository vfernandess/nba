package com.voidx.nba.feature.home.presentation

import com.voidx.nba.domain.model.Team
import com.voidx.nba.domain.model.TeamSort

sealed class HomeCommand {

    data object Load : HomeCommand()

    data object Retry : HomeCommand()

    data class SelectSort(val sort: TeamSort) : HomeCommand()

    data class SelectTeam(val team: Team) : HomeCommand()
}
