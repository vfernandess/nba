package com.voidx.nba.core.fuse.compose.ext

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReachedEndTest {

    @Test
    fun shouldLoadMore_withinBuffer_returnsTrue() {
        assertTrue(shouldLoadMore(lastVisibleIndex = 14, totalItems = 20, buffer = 5))
    }

    @Test
    fun shouldLoadMore_outsideBuffer_returnsFalse() {
        assertFalse(shouldLoadMore(lastVisibleIndex = 10, totalItems = 20, buffer = 5))
    }

    @Test
    fun shouldLoadMore_emptyList_returnsFalse() {
        assertFalse(shouldLoadMore(lastVisibleIndex = -1, totalItems = 0, buffer = 5))
    }
}
