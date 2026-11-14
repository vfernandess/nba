package com.voidx.nba.core.di

import com.voidx.nba.core.network.KeyProvider
import com.voidx.nba.core.network.NativeKeyProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class KeyModule {
    @Binds
    abstract fun bindKeyProvider(impl: NativeKeyProvider): KeyProvider
}
