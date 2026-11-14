package com.voidx.nba.core.network

interface KeyProvider {
    fun apiKey(): String
    fun baseUrl(): String
}
