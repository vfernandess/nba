package com.voidx.nba.feature.home

import com.voidx.nba.domain.model.Team

internal val hawks = Team(id = 1, fullName = "Atlanta Hawks", city = "Atlanta", conference = "East")
internal val bulls = Team(id = 4, fullName = "Chicago Bulls", city = "Chicago", conference = "East")
internal val lakers = Team(id = 14, fullName = "Los Angeles Lakers", city = "Los Angeles", conference = "West")
internal val celtics = Team(id = 2, fullName = "Boston Celtics", city = "Boston", conference = "East")

internal val unsortedTeams = listOf(hawks, bulls, lakers, celtics)
