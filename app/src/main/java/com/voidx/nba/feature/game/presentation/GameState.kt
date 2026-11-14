package com.voidx.nba.feature.game.presentation

import androidx.compose.runtime.Stable
import com.voidx.nba.core.common.AppError
import com.voidx.nba.core.ui.ContentState
import com.voidx.nba.domain.model.Game
import com.voidx.nba.feature.game.GameOrigin

@Stable
data class GameState(
    val teamId: Int = 0,
    val teamName: String = "",
    val origin: GameOrigin = GameOrigin.Home,
    val content: ContentState<List<Game>> = ContentState.Loading,
    val next: GameCursor? = null,
    val isLoadingMore: Boolean = false,
    val loadMoreError: AppError? = null,
)
