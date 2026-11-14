package com.voidx.nba.data.repository

import com.voidx.nba.domain.model.Page
import com.voidx.nba.domain.model.Player
import com.voidx.nba.domain.repository.PlayerRepository
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor() : PlayerRepository {
    override suspend fun searchPlayers(query: String, cursor: Int?, perPage: Int): Page<Player> =
        throw NotImplementedError("Task 5")
}
