package com.voidx.nba.feature.player

import com.voidx.nba.domain.model.Page
import com.voidx.nba.domain.model.Player
import com.voidx.nba.domain.model.Team
import com.voidx.nba.domain.repository.PlayerRepository

class FakePlayerRepository(
    var handler: suspend (query: String, cursor: Int?, perPage: Int) -> Page<Player> =
        { _, _, _ -> Page(emptyList(), null) },
) : PlayerRepository {

    data class Call(val query: String, val cursor: Int?, val perPage: Int)

    val calls = mutableListOf<Call>()

    override suspend fun searchPlayers(query: String, cursor: Int?, perPage: Int): Page<Player> {
        calls += Call(query, cursor, perPage)
        return handler(query, cursor, perPage)
    }
}

fun testPlayer(id: Int, firstName: String = "First$id", lastName: String = "Last$id") = Player(
    id = id,
    firstName = firstName,
    lastName = lastName,
    team = Team(id = 10 + id, fullName = "Team ${10 + id}", name = "${10 + id}", city = "City", conference = "East"),
)
