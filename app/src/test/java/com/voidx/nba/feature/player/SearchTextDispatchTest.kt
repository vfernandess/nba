package com.voidx.nba.feature.player

import com.voidx.nba.feature.player.view.searchesToDispatch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchTextDispatchTest {

    @Test
    fun restoredTextMatchingViewModelQuery_isNotSearchedAgain() = runTest {
        // Rotation / returning to the tab: the ViewModel already holds the text.
        val searches = flowOf("lebron", "lebro").searchesToDispatch { "lebron" }.toList()

        assertEquals(listOf("lebro"), searches)
    }

    @Test
    fun restoredTextAfterProcessDeath_isSearched() = runTest {
        // Process death: the field restores "lebron" but the new ViewModel starts with "".
        val searches = flowOf("lebron").searchesToDispatch { "" }.toList()

        assertEquals(listOf("lebron"), searches)
    }

    @Test
    fun initialEmptyText_withEmptyQuery_isNotSearched() = runTest {
        val searches = flowOf("", "l").searchesToDispatch { "" }.toList()

        assertEquals(listOf("l"), searches)
    }

    @Test
    fun laterEdits_areAlwaysSearched_evenIfViewModelLagsBehind() = runTest {
        val searches = flowOf("a", "ab", "a").searchesToDispatch { "a" }.toList()

        assertEquals(listOf("ab", "a"), searches)
    }
}
