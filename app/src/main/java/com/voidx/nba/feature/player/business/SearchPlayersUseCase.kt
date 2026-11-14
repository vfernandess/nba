package com.voidx.nba.feature.player.business

import com.voidx.nba.core.common.AppError
import com.voidx.nba.core.common.AppException
import com.voidx.nba.domain.repository.PlayerRepository
import com.voidx.nba.feature.player.presentation.PLAYERS_PER_PAGE
import com.voidx.nba.feature.player.presentation.PlayerEffect
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface SearchPlayersUseCase {

    /** [cursor] null means the first page. */
    operator fun invoke(query: String, cursor: Int?): Flow<PlayerEffect>

    class Impl @Inject constructor(
        private val repository: PlayerRepository,
    ) : SearchPlayersUseCase {

        override fun invoke(query: String, cursor: Int?): Flow<PlayerEffect> = flow {
            val isFirstPage = cursor == null
            emit(if (isFirstPage) PlayerEffect.Loading else PlayerEffect.LoadingMore)
            try {
                val page = repository.searchPlayers(query, cursor, PLAYERS_PER_PAGE)
                if (isFirstPage && page.items.isEmpty()) {
                    emit(PlayerEffect.ShowNoResults(query))
                } else {
                    emit(PlayerEffect.ShowPage(page.items, page.nextCursor, append = !isFirstPage))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val error = (e as? AppException)?.error ?: AppError.Unknown
                emit(
                    if (isFirstPage) PlayerEffect.ShowError(error) else PlayerEffect.ShowLoadMoreError(error),
                )
            }
        }
    }
}
