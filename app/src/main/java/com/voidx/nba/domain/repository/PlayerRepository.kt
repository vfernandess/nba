package com.voidx.nba.domain.repository

import com.voidx.nba.domain.model.Page
import com.voidx.nba.domain.model.Player

interface PlayerRepository {
    suspend fun searchPlayers(query: String, cursor: Int?, perPage: Int): Page<Player>
}
