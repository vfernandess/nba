package com.voidx.nba.data

import com.voidx.nba.data.remote.BallDontLieApi
import com.voidx.nba.data.remote.HttpClientFactory
import com.voidx.nba.data.repository.GameRepositoryImpl
import com.voidx.nba.data.repository.PlayerRepositoryImpl
import com.voidx.nba.data.repository.TeamRepositoryImpl
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RepositoryImplTest {

    private fun api(body: String): BallDontLieApi {
        val engine = MockEngine {
            respond(body, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
        }
        val keys = FakeKeyProvider()
        return BallDontLieApi(HttpClientFactory.create(engine, keys, debug = false), keys)
    }

    private val team = """{"id":1,"conference":"East","city":"Atlanta","full_name":"Atlanta Hawks","name":"Hawks",""" +
        """"abbreviation":"ATL","division":"Southeast"}"""

    private fun game(id: Int) = """{"id":$id,"status":"Final","season":2026,"home_team_score":100,""" +
        """"visitor_team_score":90,"home_team":$team,"visitor_team":$team}"""

    @Test
    fun gameRepository_mapsPage() = runTest {
        val body = """{"data":[${game(1)},${game(2)}],"meta":{"next_cursor":77,"per_page":100}}"""

        val page = GameRepositoryImpl(api(body)).getGames(teamId = 1, season = 2026, cursor = null, perPage = 100)

        assertEquals(2, page.items.size)
        assertEquals(77, page.nextCursor)
        assertEquals(true, page.items.first().isFinal)
    }

    @Test
    fun playerRepository_mapsPageAndLastPage() = runTest {
        val body = """{"data":[{"id":5,"first_name":"LeBron","last_name":"James","team":$team}],"meta":{"per_page":25}}"""

        val page = PlayerRepositoryImpl(api(body)).searchPlayers("lebron", cursor = null, perPage = 25)

        assertEquals("LeBron", page.items.single().firstName)
        assertNull(page.nextCursor)
    }

    @Test
    fun teamRepository_mapsTeams() = runTest {
        val teams = TeamRepositoryImpl(api("""{"data":[$team]}""")).getTeams()

        assertEquals("Atlanta Hawks", teams.single().fullName)
    }
}
