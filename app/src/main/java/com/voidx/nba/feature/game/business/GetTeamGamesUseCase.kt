package com.voidx.nba.feature.game.business

import com.voidx.nba.core.common.AppException
import com.voidx.nba.domain.model.Game
import com.voidx.nba.domain.repository.GameRepository
import com.voidx.nba.feature.game.presentation.GameCursor
import com.voidx.nba.feature.game.presentation.GameEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

const val GAMES_PER_PAGE = 100
const val FIRST_SEASON = 1946
const val MAX_EMPTY_SEASONS_IN_A_ROW = 3

// A team plays at most ~110 games a season (82 + playoffs): two pages of 100. Three leaves headroom while
// guaranteeing a misbehaving cursor can't spend the whole request quota.
const val MAX_PAGES_PER_SEASON = 3

interface GetTeamGamesUseCase {

    operator fun invoke(teamId: Int, from: GameCursor, append: Boolean): Flow<GameEffect>

    /**
     * Loads one whole season per call, newest game first. The API only returns games oldest first and a
     * season (with playoffs) can spill past one page, so a season is fetched in full before it is sorted.
     * Games after today are left out: they have no score yet and would push the latest results down.
     */
    class Impl @Inject constructor(
        private val repository: GameRepository,
        private val today: Today,
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
                val games = try {
                    loadSeason(teamId, cursor.season)
                } catch (e: AppException) {
                    emit(if (append) GameEffect.ShowLoadMoreError(e.error) else GameEffect.ShowError(e.error))
                    return@flow
                }
                if (games.isEmpty()) {
                    cursor = GameCursor(cursor.season - 1, cursor.emptySeasonsInARow + 1)
                    continue
                }
                val next = GameCursor(cursor.season - 1).takeIf { it.season >= FIRST_SEASON }
                emit(GameEffect.ShowPage(games.sortedByDescending { it.date }, next, append))
                return@flow
            }
        }

        private suspend fun loadSeason(teamId: Int, season: Int): List<Game> {
            val endDate = today()
            val games = mutableListOf<Game>()
            var pageCursor: Int? = null
            var pages = 0
            do {
                val page = repository.getGames(teamId, season, pageCursor, GAMES_PER_PAGE, endDate)
                pages++
                games += page.items
                val advanced = page.items.isNotEmpty() && page.nextCursor != pageCursor
                pageCursor = page.nextCursor.takeIf { advanced && pages < MAX_PAGES_PER_SEASON }
            } while (pageCursor != null)
            // Pages fetched seconds apart can overlap; a repeated id would crash the list's `key = { it.id }`.
            return games.distinctBy { it.id }
        }
    }
}
