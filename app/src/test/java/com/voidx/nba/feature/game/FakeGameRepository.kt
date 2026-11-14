package com.voidx.nba.feature.game

import com.voidx.nba.core.common.AppException
import com.voidx.nba.domain.model.Game
import com.voidx.nba.domain.model.Page
import com.voidx.nba.domain.repository.GameRepository

class FakeGameRepository(
    private val pages: Map<Pair<Int, Int?>, Page<Game>> = emptyMap(),
    private val failure: AppException? = null,
) : GameRepository {

    data class Call(val teamId: Int, val season: Int, val cursor: Int?, val perPage: Int)

    val calls = mutableListOf<Call>()

    override suspend fun getGames(teamId: Int, season: Int, cursor: Int?, perPage: Int): Page<Game> {
        calls += Call(teamId, season, cursor, perPage)
        failure?.let { throw it }
        return pages[season to cursor] ?: Page(emptyList(), null)
    }
}

fun game(id: Int, isFinal: Boolean = true) = Game(
    id = id,
    homeTeamName = "Home $id",
    homeTeamScore = 100 + id,
    visitorTeamName = "Visitor $id",
    visitorTeamScore = 90 + id,
    isFinal = isFinal,
)
