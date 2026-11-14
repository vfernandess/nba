package com.voidx.nba.core.fuse

import kotlinx.coroutines.flow.Flow

interface Interactor<State, Command, Effect> {

    suspend operator fun invoke(state: State, command: Command): Flow<Effect>
}
