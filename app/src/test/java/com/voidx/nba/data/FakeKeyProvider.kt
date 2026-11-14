package com.voidx.nba.data

import com.voidx.nba.core.network.KeyProvider

class FakeKeyProvider(
    private val apiKey: String = "k",
    private val baseUrl: String = "https://api.balldontlie.io/v1/",
) : KeyProvider {
    override fun apiKey(): String = apiKey
    override fun baseUrl(): String = baseUrl
}
