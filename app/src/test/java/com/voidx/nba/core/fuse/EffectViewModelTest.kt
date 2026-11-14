package com.voidx.nba.core.fuse

import com.voidx.nba.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EffectViewModelTest {

    @get:Rule
    val rule = MainDispatcherRule()

    private class TestReducer : SimpleReducer<String, String>("") {
        override fun reduce(effect: String, currentState: String): String = effect
    }

    private class FakeInteractor : Interactor<String, String, String> {
        override suspend fun invoke(state: String, command: String): Flow<String> =
            if (command == "slow") {
                flow {
                    delay(1_000)
                    emit("slow")
                }
            } else {
                flowOf(command)
            }
    }

    private fun viewModel() = EffectViewModel<String, String, TestReducer, String>(
        runContext = rule.dispatcher,
        interactor = FakeInteractor(),
        reducer = TestReducer()
    )

    @Test
    fun dispatch_withSameCancelKey_cancelsPreviousJob() = runTest(rule.dispatcher) {
        val vm = viewModel()

        vm.dispatch("slow", cancelKey = "k")
        vm.dispatch("fast", cancelKey = "k")
        advanceUntilIdle()

        assertEquals("fast", vm.state.value)
    }

    @Test
    fun dispatch_withoutKey_runsBoth() = runTest(rule.dispatcher) {
        val vm = viewModel()

        vm.dispatch("slow")
        vm.dispatch("fast")
        advanceUntilIdle()

        assertEquals("slow", vm.state.value)
    }
}
