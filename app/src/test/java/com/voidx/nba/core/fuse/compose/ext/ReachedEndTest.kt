package com.voidx.nba.core.fuse.compose.ext

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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

    @Test
    fun reachedEndSignals_refiresWhenItemCountChangesWhileStillAtEnd() = runTest {
        val signals = flowOf(
            ListEnd(totalItems = 101, atEnd = false),
            ListEnd(totalItems = 101, atEnd = true),
            ListEnd(totalItems = 101, atEnd = true),
            // A short page (1 game) is appended and the user is still at the end.
            ListEnd(totalItems = 102, atEnd = true),
        ).reachedEndSignals().toList()

        assertEquals(listOf(101, 102), signals)
    }

    @Test
    fun reachedEndSignals_ignoresScrollingAwayAndFiresAgainOnReturn() = runTest {
        val signals = flowOf(
            ListEnd(totalItems = 50, atEnd = true),
            ListEnd(totalItems = 50, atEnd = false),
            ListEnd(totalItems = 50, atEnd = true),
        ).reachedEndSignals().toList()

        assertEquals(listOf(50, 50), signals)
    }

    @Test
    fun reachedEndSignals_neverFiresWhenNotAtEnd() = runTest {
        val signals = flowOf(
            ListEnd(totalItems = 0, atEnd = false),
            ListEnd(totalItems = 20, atEnd = false),
        ).reachedEndSignals().toList()

        assertEquals(emptyList<Int>(), signals)
    }
}
