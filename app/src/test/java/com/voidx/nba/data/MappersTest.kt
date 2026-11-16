package com.voidx.nba.data

import com.voidx.nba.data.mapper.toDomain
import com.voidx.nba.data.mapper.toPage
import com.voidx.nba.data.remote.dto.GameDto
import com.voidx.nba.data.remote.dto.MetaDto
import com.voidx.nba.data.remote.dto.PageDto
import com.voidx.nba.data.remote.dto.PlayerDto
import com.voidx.nba.data.remote.dto.TeamDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MappersTest {

    private val hawks = TeamDto(id = 1, conference = "East", city = "Atlanta", name = "Hawks", fullName = "Atlanta Hawks")
    private val celtics = TeamDto(id = 2, conference = "East", city = "Boston", fullName = "Boston Celtics")

    private fun game(status: String) = GameDto(
        id = 9,
        status = status,
        homeTeamScore = 110,
        visitorTeamScore = 100,
        homeTeam = hawks,
        visitorTeam = celtics,
    )

    @Test
    fun finalStatus_isFinal() {
        val game = game("Final").toDomain()

        assertTrue(game.isFinal)
        assertEquals("Atlanta Hawks", game.homeTeamName)
        assertEquals("Boston Celtics", game.visitorTeamName)
        assertEquals(110, game.homeTeamScore)
        assertEquals(100, game.visitorTeamScore)
    }

    @Test
    fun scheduledStatus_isNotFinal() {
        assertFalse(game("2026-11-20T00:00:00Z").toDomain().isFinal)
    }

    @Test
    fun team_mapsFields() {
        val team = hawks.toDomain()

        assertEquals("Hawks", team.name)

        assertEquals(1, team.id)
        assertEquals("Atlanta Hawks", team.fullName)
        assertEquals("Atlanta", team.city)
        assertEquals("East", team.conference)
    }

    @Test
    fun player_mapsTeam() {
        val player = PlayerDto(id = 3, firstName = "LeBron", lastName = "James", team = hawks).toDomain()

        assertEquals("LeBron", player.firstName)
        assertEquals("James", player.lastName)
        assertEquals("Atlanta Hawks", player.team.fullName)
    }

    @Test
    fun page_withoutNextCursor_hasNullCursor() {
        val page = PageDto(data = listOf(hawks), meta = MetaDto(nextCursor = null)).toPage { it.toDomain() }

        assertNull(page.nextCursor)
        assertEquals(1, page.items.size)
    }

    @Test
    fun page_withoutMeta_hasNullCursor() {
        assertNull(PageDto(data = listOf(hawks)).toPage { it.toDomain() }.nextCursor)
    }

    @Test
    fun page_withNextCursor_keepsIt() {
        val page = PageDto(data = listOf(hawks), meta = MetaDto(nextCursor = 77)).toPage { it.toDomain() }

        assertEquals(77, page.nextCursor)
    }
}
