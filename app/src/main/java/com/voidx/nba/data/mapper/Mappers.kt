package com.voidx.nba.data.mapper

import com.voidx.nba.data.remote.dto.GameDto
import com.voidx.nba.data.remote.dto.PageDto
import com.voidx.nba.data.remote.dto.PlayerDto
import com.voidx.nba.data.remote.dto.TeamDto
import com.voidx.nba.domain.model.Game
import com.voidx.nba.domain.model.Page
import com.voidx.nba.domain.model.Player
import com.voidx.nba.domain.model.Team
import java.time.LocalDate

private const val STATUS_FINAL = "Final"
private const val ISO_DATE_LENGTH = 10

fun TeamDto.toDomain(): Team = Team(
    id = id,
    fullName = fullName,
    name = name,
    city = city,
    conference = conference,
)

fun GameDto.toDomain(): Game = Game(
    id = id,
    // The API sends "2026-11-14" (older payloads append a time), so keep only the date part.
    date = runCatching { LocalDate.parse(date.take(ISO_DATE_LENGTH)) }.getOrDefault(LocalDate.MIN),
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
