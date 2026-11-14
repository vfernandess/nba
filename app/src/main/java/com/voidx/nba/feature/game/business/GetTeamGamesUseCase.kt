package com.voidx.nba.feature.game.business

import com.voidx.nba.core.common.AppException
import com.voidx.nba.domain.model.Page
import com.voidx.nba.domain.repository.GameRepository
import com.voidx.nba.feature.game.presentation.GameCursor
import com.voidx.nba.feature.game.presentation.GameEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

const val GAMES_PER_PAGE = 100
const val FIRST_SEASON = 1946
const val MAX_EMPTY_SEASONS_IN_A_ROW = 3

interface GetTeamGamesUseCase {

    operator fun invoke(teamId: Int, from: GameCursor, append: Boolean): Flow<GameEffect>

    class Impl @Inject constructor(
        private val repository: GameRepository,
    ) : GetTeamGamesUseCase {

        override fun invoke(teamId: Int, from: GameCursor, append: Boolean): Flow<GameEffect> = flow {
            emit(if (append) GameEffect.LoadingMore else GameEffect.Loading)
            var cursor = from
            while (true) {
                if (cursor.season < FIRST_SEASON || cursor.emptySeasonsInARow >= MAX_EMPTY_SEASONS_IN_A_ROW) {
                    emit(
                        if (append) GameEffect.ShowPage(emptyList(), next = null, append = true)
                        else GameEffect.ShowEmpty
                    )
                    return@flow
                }
                val page = try {
                    repository.getGames(teamId, cursor.season, cursor.cursor, GAMES_PER_PAGE)
                } catch (e: AppException) {
                    emit(if (append) GameEffect.ShowLoadMoreError(e.error) else GameEffect.ShowError(e.error))
                    return@flow
                }
                if (page.items.isEmpty() && cursor.cursor == null) {
                    cursor = GameCursor(cursor.season - 1, null, cursor.emptySeasonsInARow + 1)
                    continue
                }
                emit(GameEffect.ShowPage(page.items, nextCursorAfter(cursor, page), append))
                return@flow
            }
        }

        private fun nextCursorAfter(current: GameCursor, page: Page<*>): GameCursor? =
            page.nextCursor?.let { GameCursor(current.season, it) }
                ?: GameCursor(current.season - 1).takeIf { it.season >= FIRST_SEASON }
    }
}
