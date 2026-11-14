package com.voidx.nba.feature.player.presentation

sealed class PlayerSideEffect {

    data class OpenTeam(val teamId: Int, val teamName: String) : PlayerSideEffect()
}
