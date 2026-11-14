package com.voidx.nba.data.repository

import com.voidx.nba.domain.model.Team
import com.voidx.nba.domain.repository.TeamRepository
import javax.inject.Inject

class TeamRepositoryImpl @Inject constructor() : TeamRepository {
    override suspend fun getTeams(): List<Team> = throw NotImplementedError("Task 5")
}
