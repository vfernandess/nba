package com.voidx.nba.core.di

import com.voidx.nba.BuildConfig
import com.voidx.nba.core.network.KeyProvider
import com.voidx.nba.data.remote.HttpClientFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideHttpClient(keys: KeyProvider): HttpClient =
        HttpClientFactory.create(OkHttp.create(), keys, BuildConfig.DEBUG)
}
