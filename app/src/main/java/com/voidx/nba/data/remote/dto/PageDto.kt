package com.voidx.nba.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MetaDto(
    @SerialName("next_cursor") val nextCursor: Int? = null,
    @SerialName("per_page") val perPage: Int = 25,
)

@Serializable
data class PageDto<T>(
    val data: List<T>,
    val meta: MetaDto? = null,
)
