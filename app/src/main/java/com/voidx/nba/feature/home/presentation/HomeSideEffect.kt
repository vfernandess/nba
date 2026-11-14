package com.voidx.nba.feature.home.presentation

sealed class HomeSideEffect {

    data class OpenTeam(val teamId: Int, val teamName: String) : HomeSideEffect()
}
