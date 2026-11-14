package com.voidx.nba.data.repository

import com.voidx.nba.data.mapper.toDomain
import com.voidx.nba.data.mapper.toPage
import com.voidx.nba.data.remote.BallDontLieApi
import com.voidx.nba.domain.model.Page
import com.voidx.nba.domain.model.Player
import com.voidx.nba.domain.repository.PlayerRepository
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(
    private val api: BallDontLieApi,
) : PlayerRepository {
    override suspend fun searchPlayers(query: String, cursor: Int?, perPage: Int): Page<Player> =
        api.players(query, cursor, perPage).toPage { it.toDomain() }
}
