package com.voidx.nba.feature.player

import com.voidx.nba.core.common.AppError
import com.voidx.nba.core.ui.ContentState
import com.voidx.nba.feature.player.presentation.PlayerState
import com.voidx.nba.feature.player.presentation.canLoadMore
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayerStateTest {

    private val ready = PlayerState(
        query = " le ",
        resultsQuery = "le",
        content = ContentState.Success(listOf(testPlayer(1))),
        nextCursor = 25,
    )

    @Test
    fun canLoadMore_trueWhenResultsMatchQueryAndMoreAvailable() {
        assertTrue(ready.canLoadMore())
    }

    @Test
    fun canLoadMore_falseWhenCursorNull() {
        assertFalse(ready.copy(nextCursor = null).canLoadMore())
    }

    @Test
    fun canLoadMore_falseWhileLoadingMore() {
        assertFalse(ready.copy(isLoadingMore = true).canLoadMore())
    }

    @Test
    fun canLoadMore_falseWhenQueryDiffersFromResultsQuery() {
        assertFalse(ready.copy(query = "lebron").canLoadMore())
    }

    @Test
    fun canLoadMore_falseWhenNotSuccess() {
        assertFalse(ready.copy(content = ContentState.Loading).canLoadMore())
        assertFalse(ready.copy(content = ContentState.Error(AppError.Network)).canLoadMore())
    }

    @Test
    fun canLoadMore_trueWithLoadMoreErrorSoFooterRetryWorks() {
        assertTrue(ready.copy(loadMoreError = AppError.Network).canLoadMore())
    }
}
