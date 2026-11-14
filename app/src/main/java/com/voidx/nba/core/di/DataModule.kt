package com.voidx.nba.core.di

import com.voidx.nba.data.repository.GameRepositoryImpl
import com.voidx.nba.data.repository.PlayerRepositoryImpl
import com.voidx.nba.data.repository.TeamRepositoryImpl
import com.voidx.nba.domain.repository.GameRepository
import com.voidx.nba.domain.repository.PlayerRepository
import com.voidx.nba.domain.repository.TeamRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindTeamRepository(impl: TeamRepositoryImpl): TeamRepository

    @Binds
    @Singleton
    abstract fun bindGameRepository(impl: GameRepositoryImpl): GameRepository

    @Binds
    @Singleton
    abstract fun bindPlayerRepository(impl: PlayerRepositoryImpl): PlayerRepository
}
