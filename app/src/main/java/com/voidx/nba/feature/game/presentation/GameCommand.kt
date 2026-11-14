package com.voidx.nba.feature.game.presentation

import com.voidx.nba.feature.game.GameOrigin

sealed class GameCommand {

    data class Start(val teamId: Int, val teamName: String, val origin: GameOrigin) : GameCommand()

    data object Retry : GameCommand()

    data object LoadMore : GameCommand()
}
