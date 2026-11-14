package com.voidx.nba.feature.game.presentation

data class GameCursor(
    val season: Int,
    val cursor: Int? = null,
    val emptySeasonsInARow: Int = 0,
)
