package com.voidx.nba.data.repository

import com.voidx.nba.data.mapper.toDomain
import com.voidx.nba.data.mapper.toPage
import com.voidx.nba.data.remote.BallDontLieApi
import com.voidx.nba.domain.model.Game
import com.voidx.nba.domain.model.Page
import com.voidx.nba.domain.repository.GameRepository
import java.time.LocalDate
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val api: BallDontLieApi,
) : GameRepository {
    override suspend fun getGames(
        teamId: Int,
        season: Int,
        cursor: Int?,
        perPage: Int,
        endDate: LocalDate,
    ): Page<Game> =
        api.games(teamId, season, cursor, perPage, endDate).toPage { it.toDomain() }
}
