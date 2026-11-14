package com.voidx.nba.feature.home

import com.voidx.nba.domain.model.Team
import com.voidx.nba.domain.repository.TeamRepository

class FakeTeamRepository(
    var teams: List<Team> = emptyList(),
    var failure: Throwable? = null,
) : TeamRepository {

    var callCount: Int = 0
        private set

    override suspend fun getTeams(): List<Team> {
        callCount++
        failure?.let { throw it }
        return teams
    }
}
