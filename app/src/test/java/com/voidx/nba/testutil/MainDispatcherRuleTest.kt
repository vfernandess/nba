package com.voidx.nba.testutil

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MainDispatcherRuleTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `main dispatcher is replaced by the rule dispatcher`() = runTest(mainDispatcherRule.dispatcher) {
        var ran = false

        withContext(Dispatchers.Main) { ran = true }

        assertEquals(true, ran)
    }
}
