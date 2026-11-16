package com.voidx.nba.domain.repository

import com.voidx.nba.domain.model.Game
import com.voidx.nba.domain.model.Page
import java.time.LocalDate

interface GameRepository {
    suspend fun getGames(teamId: Int, season: Int, cursor: Int?, perPage: Int, endDate: LocalDate): Page<Game>
}
