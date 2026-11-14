package com.voidx.nba.feature.player.presentation

import com.voidx.nba.core.common.AppError
import com.voidx.nba.domain.model.Player

sealed class PlayerEffect {

    data class QueryChanged(val query: String) : PlayerEffect()

    data object ShowPrompt : PlayerEffect()

    data object Loading : PlayerEffect()

    data object LoadingMore : PlayerEffect()

    data class ShowPage(
        val players: List<Player>,
        val nextCursor: Int?,
        val append: Boolean,
    ) : PlayerEffect()

    data class ShowNoResults(val query: String) : PlayerEffect()

    data class ShowError(val error: AppError) : PlayerEffect()

    data class ShowLoadMoreError(val error: AppError) : PlayerEffect()

    /** Carrier for the OpenTeam side effect; the reducer ignores it. */
    data class PlayerSelected(val player: Player) : PlayerEffect()
}
