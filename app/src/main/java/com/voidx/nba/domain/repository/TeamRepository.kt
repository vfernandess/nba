package com.voidx.nba.domain.repository

import com.voidx.nba.domain.model.Team

interface TeamRepository {
    suspend fun getTeams(): List<Team>
}
