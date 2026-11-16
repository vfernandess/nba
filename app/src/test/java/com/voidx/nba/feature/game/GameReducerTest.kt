package com.voidx.nba.feature.game

import com.voidx.nba.core.common.AppError
import com.voidx.nba.core.ui.ContentState
import com.voidx.nba.core.ui.EmptyReason
import com.voidx.nba.feature.game.presentation.GameCursor
import com.voidx.nba.feature.game.presentation.GameEffect
import com.voidx.nba.feature.game.presentation.GameReducer
import com.voidx.nba.feature.game.presentation.GameState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameReducerTest {

    private val reducer = GameReducer.Impl()
    private val g1 = listOf(game(1))
    private val g2 = listOf(game(2))

    private fun reduce(effect: GameEffect, state: GameState = GameState()) = reducer.reduce(effect, state)

    @Test
    fun started_setsTeamFields() {
        val state = reduce(GameEffect.Started(14, "Atlanta Hawks"))

        assertEquals(14, state.teamId)
        assertEquals("Atlanta Hawks", state.teamName)
    }

    @Test
    fun loading_resetsToLoadingContent() {
        val previous = GameState(content = ContentState.Success(g1), next = GameCursor(2025), isLoadingMore = true)

        val state = reduce(GameEffect.Loading, previous)

        assertEquals(ContentState.Loading, state.content)
        assertNull(state.next)
        assertFalse(state.isLoadingMore)
    }

    @Test
    fun showEmpty_setsNoDataEmpty() {
        assertEquals(ContentState.Empty(EmptyReason.NoData), reduce(GameEffect.ShowEmpty).content)
    }

    @Test
    fun showError_setsErrorContent() {
        assertEquals(ContentState.Error(AppError.Network), reduce(GameEffect.ShowError(AppError.Network)).content)
    }

    @Test
    fun showPage_withoutAppend_replacesGamesAndStoresNext() {
        val state = reduce(GameEffect.ShowPage(g1, GameCursor(2026), append = false))

        assertEquals(ContentState.Success(g1), state.content)
        assertEquals(GameCursor(2026), state.next)
        assertFalse(state.isLoadingMore)
    }

    @Test
    fun showPage_withAppend_appendsGames() {
        val previous = GameState(content = ContentState.Success(g1), isLoadingMore = true)

        val state = reduce(GameEffect.ShowPage(g2, GameCursor(2025), append = true), previous)

        assertEquals(ContentState.Success(g1 + g2), state.content)
        assertEquals(GameCursor(2025), state.next)
        assertFalse(state.isLoadingMore)
    }

    @Test
    fun showPage_withAppend_dropsGamesAlreadyShown() {
        val previous = GameState(content = ContentState.Success(g1 + g2), isLoadingMore = true)

        val state = reduce(GameEffect.ShowPage(g2 + listOf(game(3)), GameCursor(2024), append = true), previous)

        assertEquals(ContentState.Success(g1 + g2 + listOf(game(3))), state.content)
    }

    @Test
    fun loadingMore_setsFooterLoadingAndClearsError() {
        val previous = GameState(content = ContentState.Success(g1), loadMoreError = AppError.Network)

        val state = reduce(GameEffect.LoadingMore, previous)

        assertTrue(state.isLoadingMore)
        assertNull(state.loadMoreError)
        assertEquals(ContentState.Success(g1), state.content)
    }

    @Test
    fun loadMoreError_keepsSuccessAndSetsFooterError() {
        val previous = GameState(content = ContentState.Success(g1), isLoadingMore = true)

        val state = reduce(GameEffect.ShowLoadMoreError(AppError.RateLimited), previous)

        assertEquals(ContentState.Success(g1), state.content)
        assertEquals(AppError.RateLimited, state.loadMoreError)
        assertFalse(state.isLoadingMore)
    }
}
