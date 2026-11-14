package com.voidx.nba.domain.model

data class Page<T>(
    val items: List<T>,
    val nextCursor: Int?,
)
