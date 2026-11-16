package com.voidx.nba.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameDto(
    val id: Int,
    val date: String = "",
    val status: String = "",
    val season: Int = 0,
    @SerialName("home_team_score") val homeTeamScore: Int = 0,
    @SerialName("visitor_team_score") val visitorTeamScore: Int = 0,
    @SerialName("home_team") val homeTeam: TeamDto,
    @SerialName("visitor_team") val visitorTeam: TeamDto,
)
