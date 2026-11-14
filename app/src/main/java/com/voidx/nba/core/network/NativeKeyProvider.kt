package com.voidx.nba.core.network

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NativeKeyProvider @Inject constructor() : KeyProvider {
    override fun apiKey(): String = ""
    override fun baseUrl(): String = "https://api.balldontlie.io/v1/"
}
