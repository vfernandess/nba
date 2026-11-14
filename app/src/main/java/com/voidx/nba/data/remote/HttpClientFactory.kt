package com.voidx.nba.data.remote

import com.voidx.nba.core.network.KeyProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val MAX_RETRIES = 2
private const val SERVER_ERROR_START = 500
private const val TOO_MANY_REQUESTS = 429

object HttpClientFactory {
    fun create(engine: HttpClientEngine, keys: KeyProvider, debug: Boolean): HttpClient =
        HttpClient(engine) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        explicitNulls = false
                    },
                )
            }
            install(HttpRequestRetry) {
                retryIf(maxRetries = MAX_RETRIES) { _, response ->
                    response.status.value == TOO_MANY_REQUESTS || response.status.value >= SERVER_ERROR_START
                }
                retryOnExceptionIf(maxRetries = MAX_RETRIES) { _, _ -> false }
                exponentialDelay()
            }
            if (debug) {
                install(Logging) { level = LogLevel.INFO }
            }
            defaultRequest {
                url(keys.baseUrl())
                header(HttpHeaders.Authorization, keys.apiKey())
            }
        }
}
