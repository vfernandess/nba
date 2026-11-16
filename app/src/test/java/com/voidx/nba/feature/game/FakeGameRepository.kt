package com.voidx.nba.feature.game

import com.voidx.nba.core.common.AppException
import com.voidx.nba.domain.model.Game
import com.voidx.nba.domain.model.Page
import com.voidx.nba.domain.repository.GameRepository
import java.time.LocalDate

class FakeGameRepository(
    private val pages: Map<Pair<Int, Int?>, Page<Game>> = emptyMap(),
    private val failure: AppException? = null,
    private val failOn: Pair<Int, Int?>? = null,
) : GameRepository {

    data class Call(val teamId: Int, val season: Int, val cursor: Int?, val perPage: Int, val endDate: LocalDate)

    val calls = mutableListOf<Call>()

    override suspend fun getGames(
        teamId: Int,
        season: Int,
        cursor: Int?,
        perPage: Int,
        endDate: LocalDate,
    ): Page<Game> {
        calls += Call(teamId, season, cursor, perPage, endDate)
        failure?.let { if (failOn == null || failOn == season to cursor) throw it }
        return pages[season to cursor] ?: Page(emptyList(), null)
    }
}

fun game(id: Int, isFinal: Boolean = true, date: LocalDate = LocalDate.of(2026, 1, 1)) = Game(
    id = id,
    date = date,
    homeTeamName = "Home $id",
    homeTeamScore = 100 + id,
    visitorTeamName = "Visitor $id",
    visitorTeamScore = 90 + id,
    isFinal = isFinal,
)
