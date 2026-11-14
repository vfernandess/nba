package com.voidx.nba.feature.home

import com.voidx.nba.domain.model.Team
import com.voidx.nba.domain.model.TeamSort
import com.voidx.nba.feature.home.business.SortTeamsUseCase
import org.junit.Assert.assertEquals
import org.junit.Test

class SortTeamsUseCaseTest {

    private val useCase: SortTeamsUseCase = SortTeamsUseCase.Impl()

    @Test
    fun invoke_byName_ordersByFullName() {
        val result = useCase(unsortedTeams, TeamSort.Name)

        assertEquals(
            listOf("Atlanta Hawks", "Boston Celtics", "Chicago Bulls", "Los Angeles Lakers"),
            result.map { it.fullName },
        )
    }

    @Test
    fun invoke_byCity_ordersByCity() {
        val result = useCase(unsortedTeams, TeamSort.City)

        assertEquals(listOf("Atlanta", "Boston", "Chicago", "Los Angeles"), result.map { it.city })
    }

    @Test
    fun invoke_byConference_ordersEastThenWestAndByNameWithinConference() {
        val result = useCase(unsortedTeams, TeamSort.Conference)

        assertEquals(
            listOf("Atlanta Hawks", "Boston Celtics", "Chicago Bulls", "Los Angeles Lakers"),
            result.map { it.fullName },
        )
        assertEquals(listOf("East", "East", "East", "West"), result.map { it.conference })
    }

    @Test
    fun invoke_isCaseInsensitive() {
        val lowerCase = Team(id = 99, fullName = "atlanta Zephyrs", city = "atlanta", conference = "east")
        val teams = listOf(celtics, lowerCase, hawks)

        assertEquals(listOf(hawks, lowerCase, celtics), useCase(teams, TeamSort.City))
        assertEquals(listOf(hawks, lowerCase, celtics), useCase(teams, TeamSort.Name))
        assertEquals(listOf(hawks, lowerCase, celtics), useCase(teams, TeamSort.Conference))
    }
}
