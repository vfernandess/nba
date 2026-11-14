package com.voidx.nba.data.repository

import com.voidx.nba.data.mapper.toDomain
import com.voidx.nba.data.mapper.toPage
import com.voidx.nba.data.remote.BallDontLieApi
import com.voidx.nba.domain.model.Game
import com.voidx.nba.domain.model.Page
import com.voidx.nba.domain.repository.GameRepository
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val api: BallDontLieApi,
) : GameRepository {
    override suspend fun getGames(teamId: Int, season: Int, cursor: Int?, perPage: Int): Page<Game> =
        api.games(teamId, season, cursor, perPage).toPage { it.toDomain() }
}
