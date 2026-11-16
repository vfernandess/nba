package com.voidx.nba.feature.game.presentation

import com.voidx.nba.core.common.AppError
import com.voidx.nba.domain.model.Game

sealed class GameEffect {

    data class Started(val teamId: Int, val teamName: String) : GameEffect()

    data object Loading : GameEffect()

    data object LoadingMore : GameEffect()

    data class ShowPage(val games: List<Game>, val next: GameCursor?, val append: Boolean) : GameEffect()

    data object ShowEmpty : GameEffect()

    data class ShowError(val error: AppError) : GameEffect()

    data class ShowLoadMoreError(val error: AppError) : GameEffect()
}
