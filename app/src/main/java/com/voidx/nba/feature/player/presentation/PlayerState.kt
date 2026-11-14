package com.voidx.nba.feature.player.presentation

import androidx.compose.runtime.Stable
import com.voidx.nba.core.common.AppError
import com.voidx.nba.core.ui.ContentState
import com.voidx.nba.core.ui.EmptyReason
import com.voidx.nba.domain.model.Player

@Stable
data class PlayerState(
    val query: String = "",
    val content: ContentState<List<Player>> = ContentState.Empty(EmptyReason.Prompt),
    val nextCursor: Int? = null,
    val isLoadingMore: Boolean = false,
    val loadMoreError: AppError? = null,
) {
    val endReached: Boolean get() = nextCursor == null
}
