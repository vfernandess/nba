package com.voidx.nba.domain.model

import java.time.LocalDate

data class Game(
    val id: Int,
    val date: LocalDate,
    val homeTeamName: String,
    val homeTeamScore: Int,
    val visitorTeamName: String,
    val visitorTeamScore: Int,
    val isFinal: Boolean,
)
