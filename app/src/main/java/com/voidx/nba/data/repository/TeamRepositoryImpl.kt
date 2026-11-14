package com.voidx.nba.data.repository

import com.voidx.nba.data.mapper.toDomain
import com.voidx.nba.data.remote.BallDontLieApi
import com.voidx.nba.domain.model.Team
import com.voidx.nba.domain.repository.TeamRepository
import javax.inject.Inject

class TeamRepositoryImpl @Inject constructor(
    private val api: BallDontLieApi,
) : TeamRepository {
    override suspend fun getTeams(): List<Team> = api.teams().data.map { it.toDomain() }
}
