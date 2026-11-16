package com.voidx.nba.data.mapper

import com.voidx.nba.data.remote.dto.GameDto
import com.voidx.nba.data.remote.dto.PageDto
import com.voidx.nba.data.remote.dto.PlayerDto
import com.voidx.nba.data.remote.dto.TeamDto
import com.voidx.nba.domain.model.Game
import com.voidx.nba.domain.model.Page
import com.voidx.nba.domain.model.Player
import com.voidx.nba.domain.model.Team

private const val STATUS_FINAL = "Final"

fun TeamDto.toDomain(): Team = Team(
    id = id,
    fullName = fullName,
    name = name,
    city = city,
    conference = conference,
)

fun GameDto.toDomain(): Game = Game(
    id = id,
    homeTeamName = homeTeam.fullName,
    homeTeamScore = homeTeamScore,
    visitorTeamName = visitorTeam.fullName,
    visitorTeamScore = visitorTeamScore,
    isFinal = status.equals(STATUS_FINAL, ignoreCase = true),
)

fun PlayerDto.toDomain(): Player = Player(
    id = id,
    firstName = firstName,
    lastName = lastName,
    team = team.toDomain(),
)

fun <T, R> PageDto<T>.toPage(map: (T) -> R): Page<R> = Page(
    items = data.map(map),
    nextCursor = meta?.nextCursor,
)
