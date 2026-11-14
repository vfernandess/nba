package com.voidx.nba.feature.player.presentation

import com.voidx.nba.domain.model.Player

const val SEARCH_DEBOUNCE_MS = 400L
const val MIN_QUERY_LENGTH = 2
const val PLAYERS_PER_PAGE = 25
const val SEARCH_JOB_KEY = "player-search"

sealed class PlayerCommand {

    data class Search(val query: String) : PlayerCommand()

    data object LoadMore : PlayerCommand()

    data object Retry : PlayerCommand()

    data class SelectPlayer(val player: Player) : PlayerCommand()
}
