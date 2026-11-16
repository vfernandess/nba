package com.voidx.nba.feature.game.presentation

sealed class GameCommand {

    data class Start(val teamId: Int, val teamName: String) : GameCommand()

    data object Retry : GameCommand()

    data object LoadMore : GameCommand()
}
