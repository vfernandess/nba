package com.voidx.nba.data.repository

import com.voidx.nba.domain.model.Game
import com.voidx.nba.domain.model.Page
import com.voidx.nba.domain.repository.GameRepository
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor() : GameRepository {
    override suspend fun getGames(teamId: Int, season: Int, cursor: Int?, perPage: Int): Page<Game> =
        throw NotImplementedError("Task 5")
}
