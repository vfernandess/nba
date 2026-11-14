package com.voidx.nba.feature.player

import com.voidx.nba.core.common.AppError
import com.voidx.nba.core.ui.ContentState
import com.voidx.nba.core.ui.EmptyReason
import com.voidx.nba.feature.player.presentation.PlayerEffect
import com.voidx.nba.feature.player.presentation.PlayerReducer
import com.voidx.nba.feature.player.presentation.PlayerState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayerReducerTest {

    private val reducer = PlayerReducer.Impl()
    private val first = listOf(testPlayer(1), testPlayer(2))
    private val second = listOf(testPlayer(3))

    @Test
    fun initialState_isPrompt() {
        assertEquals(PlayerState(), reducer.state.value)
        assertEquals(ContentState.Empty(EmptyReason.Prompt), reducer.state.value.content)
    }

    @Test
    fun queryChanged_setsQuery() {
        reducer(PlayerEffect.QueryChanged(" le "))

        assertEquals(" le ", reducer.state.value.query)
    }

    @Test
    fun showPrompt_setsPromptContent() {
        reducer(PlayerEffect.ShowPage(first, 25, append = false))
        reducer(PlayerEffect.ShowPrompt)

        val state = reducer.state.value
        assertEquals(ContentState.Empty(EmptyReason.Prompt), state.content)
        assertTrue(state.endReached)
    }

    @Test
    fun loading_setsLoadingContent() {
        reducer(PlayerEffect.Loading)

        assertEquals(ContentState.Loading, reducer.state.value.content)
    }

    @Test
    fun loading_resetsPagingState() {
        reducer(PlayerEffect.ShowPage(first, 25, append = false))
        reducer(PlayerEffect.LoadingMore)
        reducer(PlayerEffect.Loading)

        val state = reducer.state.value
        assertEquals(null, state.nextCursor)
        assertFalse(state.isLoadingMore)
        assertEquals(null, state.loadMoreError)
    }

    @Test
    fun showPage_replaces() {
        reducer(PlayerEffect.ShowPage(first, 25, append = false))
        reducer(PlayerEffect.ShowPage(second, null, append = false))

        val state = reducer.state.value
        assertEquals(ContentState.Success(second), state.content)
        assertEquals(null, state.nextCursor)
        assertTrue(state.endReached)
    }

    @Test
    fun showPage_append_appendsAndClearsLoadingMore() {
        reducer(PlayerEffect.ShowPage(first, 25, append = false))
        reducer(PlayerEffect.LoadingMore)
        reducer(PlayerEffect.ShowPage(second, 50, append = true))

        val state = reducer.state.value
        assertEquals(ContentState.Success(first + second), state.content)
        assertEquals(50, state.nextCursor)
        assertFalse(state.isLoadingMore)
        assertFalse(state.endReached)
    }

    @Test
    fun showNoResults_setsNoResultsContent() {
        reducer(PlayerEffect.ShowNoResults("zzz"))

        assertEquals(ContentState.Empty(EmptyReason.NoResults("zzz")), reducer.state.value.content)
    }

    @Test
    fun showError_setsErrorContent() {
        reducer(PlayerEffect.ShowError(AppError.Network))

        assertEquals(ContentState.Error(AppError.Network), reducer.state.value.content)
    }

    @Test
    fun loadingMore_setsFlagAndClearsError() {
        reducer(PlayerEffect.ShowPage(first, 25, append = false))
        reducer(PlayerEffect.ShowLoadMoreError(AppError.Network))
        reducer(PlayerEffect.LoadingMore)

        val state = reducer.state.value
        assertTrue(state.isLoadingMore)
        assertEquals(null, state.loadMoreError)
        assertEquals(ContentState.Success(first), state.content)
    }

    @Test
    fun showLoadMoreError_keepsSuccessAndSetsError() {
        reducer(PlayerEffect.ShowPage(first, 25, append = false))
        reducer(PlayerEffect.LoadingMore)
        reducer(PlayerEffect.ShowLoadMoreError(AppError.RateLimited))

        val state = reducer.state.value
        assertEquals(ContentState.Success(first), state.content)
        assertEquals(AppError.RateLimited, state.loadMoreError)
        assertFalse(state.isLoadingMore)
    }
}
