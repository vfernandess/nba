package com.voidx.nba.core.fuse

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

abstract class SimpleReducer<State, Effect>(
    initialState: State
): Reducer<State, Effect> {
    private val mutableState = MutableStateFlow(initialState)

    override val state: StateFlow<State>
        get() = mutableState

    // update{} retries on contention, so effects reduced concurrently on a multi-threaded context are never lost.
    override fun invoke(effect: Effect) {
        mutableState.update { reduce(effect, it) }
    }

    abstract fun reduce(effect: Effect, currentState: State): State
}
