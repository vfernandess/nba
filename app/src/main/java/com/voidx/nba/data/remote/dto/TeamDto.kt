package com.voidx.nba.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamDto(
    val id: Int,
    val conference: String = "",
    val division: String = "",
    val city: String = "",
    val name: String = "",
    @SerialName("full_name") val fullName: String = "",
    val abbreviation: String = "",
)
