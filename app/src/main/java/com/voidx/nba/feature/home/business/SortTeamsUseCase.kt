package com.voidx.nba.feature.home.business

import com.voidx.nba.domain.model.Team
import com.voidx.nba.domain.model.TeamSort
import javax.inject.Inject

interface SortTeamsUseCase {

    operator fun invoke(teams: List<Team>, sort: TeamSort): List<Team>

    class Impl @Inject constructor() : SortTeamsUseCase {

        // Full names start with the city ("Atlanta Hawks"), so sorting them would just repeat the City sort.
        private val byName = compareBy<Team, String>(String.CASE_INSENSITIVE_ORDER) { it.name }
            .thenBy(String.CASE_INSENSITIVE_ORDER) { it.fullName }

        override fun invoke(teams: List<Team>, sort: TeamSort): List<Team> {
            val comparator = when (sort) {
                TeamSort.Name -> byName
                TeamSort.City -> compareBy<Team, String>(String.CASE_INSENSITIVE_ORDER) { it.city }.then(byName)
                TeamSort.Conference ->
                    compareBy<Team, String>(String.CASE_INSENSITIVE_ORDER) { it.conference }.then(byName)
            }
            return teams.sortedWith(comparator)
        }
    }
}
