package com.voidx.nba.core.fuse

import kotlinx.coroutines.flow.StateFlow

interface Reducer<State, Effect> {

    val state: StateFlow<State>

    operator fun invoke(effect: Effect)
}
