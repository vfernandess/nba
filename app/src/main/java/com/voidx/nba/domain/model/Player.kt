package com.voidx.nba.domain.model

data class Player(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val team: Team,
)
