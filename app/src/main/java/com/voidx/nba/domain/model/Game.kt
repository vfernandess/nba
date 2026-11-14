package com.voidx.nba.domain.model

data class Game(
    val id: Int,
    val homeTeamName: String,
    val homeTeamScore: Int,
    val visitorTeamName: String,
    val visitorTeamScore: Int,
    val isFinal: Boolean,
)
