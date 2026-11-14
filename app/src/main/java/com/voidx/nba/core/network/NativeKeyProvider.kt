package com.voidx.nba.core.network

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NativeKeyProvider @Inject constructor() : KeyProvider {

    override fun apiKey(): String = nativeApiKey()

    override fun baseUrl(): String = nativeBaseUrl()

    private external fun nativeApiKey(): String

    private external fun nativeBaseUrl(): String

    private companion object {
        init {
            System.loadLibrary("keys")
        }
    }
}
