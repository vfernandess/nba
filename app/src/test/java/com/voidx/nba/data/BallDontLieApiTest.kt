package com.voidx.nba.data

import com.voidx.nba.core.common.AppError
import com.voidx.nba.core.common.AppException
import com.voidx.nba.data.remote.BallDontLieApi
import com.voidx.nba.data.remote.HttpClientFactory
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.IOException

class BallDontLieApiTest {

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")

    private fun api(
        keys: FakeKeyProvider = FakeKeyProvider(apiKey = "k"),
        handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData,
    ): BallDontLieApi {
        val engine = MockEngine { request -> handler(request) }
        return BallDontLieApi(HttpClientFactory.create(engine, keys, debug = false), keys)
    }

    private fun MockRequestHandleScope.emptyPage() =
        respond("""{"data":[]}""", HttpStatusCode.OK, jsonHeaders)

    @Test
    fun games_sendsTeamSeasonCursorAndAuth() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            emptyPage()
        }
        val keys = FakeKeyProvider(apiKey = "k")
        val api = BallDontLieApi(HttpClientFactory.create(engine, keys, debug = false), keys)

        api.games(teamId = 14, season = 2026, cursor = 50, perPage = 100)

        val request = requireNotNull(captured)
        assertEquals("/v1/games", request.url.encodedPath)
        val params = request.url.parameters
        assertEquals("14", params["team_ids[]"])
        assertEquals("2026", params["seasons[]"])
        assertEquals("50", params["cursor"])
        assertEquals("100", params["per_page"])
        assertEquals("k", request.headers[HttpHeaders.Authorization])
    }

    @Test
    fun players_sendsSearch() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            emptyPage()
        }
        val keys = FakeKeyProvider(apiKey = "k")
        val api = BallDontLieApi(HttpClientFactory.create(engine, keys, debug = false), keys)

        api.players(search = "lebron", cursor = null, perPage = 25)

        val request = requireNotNull(captured)
        assertEquals("/v1/players", request.url.encodedPath)
        assertEquals("lebron", request.url.parameters["search"])
        assertEquals("25", request.url.parameters["per_page"])
        assertFalse(request.url.parameters.contains("cursor"))
    }

    @Test
    fun teams_parsesDataWithoutMeta() = runTest {
        val body = """{"data":[{"id":1,"conference":"East","city":"Atlanta","full_name":"Atlanta Hawks",""" +
            """"name":"Hawks","abbreviation":"ATL","division":"Southeast"}]}"""
        val api = api { respond(body, HttpStatusCode.OK, jsonHeaders) }

        val page = api.teams()

        assertEquals(1, page.data.size)
        assertEquals("Atlanta Hawks", page.data.first().fullName)
        assertNull(page.meta)
    }

    @Test
    fun status429_throwsRateLimited() = runTest {
        val api = api { respond("", HttpStatusCode.TooManyRequests) }

        assertError(AppError.RateLimited) { api.teams() }
    }

    @Test
    fun status401_throwsUnauthorized() = runTest {
        val api = api { respond("", HttpStatusCode.Unauthorized) }

        assertError(AppError.Unauthorized) { api.teams() }
    }

    @Test
    fun ioException_throwsNetwork() = runTest {
        val api = api { throw IOException("offline") }

        assertError(AppError.Network) { api.teams() }
    }

    @Test
    fun blankApiKey_throwsMissingApiKeyWithoutRequest() = runTest {
        var invoked = false
        val api = api(keys = FakeKeyProvider(apiKey = "")) {
            invoked = true
            respond("""{"data":[]}""", HttpStatusCode.OK, jsonHeaders)
        }

        assertError(AppError.MissingApiKey) { api.teams() }
        assertFalse(invoked)
    }

    @Test
    fun status429Then200_retriesAndSucceeds() = runTest {
        var calls = 0
        val api = api {
            calls++
            if (calls == 1) respond("", HttpStatusCode.TooManyRequests) else emptyPage()
        }

        val page = api.teams()

        assertEquals(2, calls)
        assertTrue(page.data.isEmpty())
    }

    @Test
    fun status429Always_retriesTwiceThenRateLimited() = runTest {
        var calls = 0
        val api = api {
            calls++
            respond("", HttpStatusCode.TooManyRequests)
        }

        assertError(AppError.RateLimited) { api.teams() }
        assertEquals(3, calls)
    }

    @Test
    fun status500Always_retriesTwiceThenUnknown() = runTest {
        var calls = 0
        val api = api {
            calls++
            respond("", HttpStatusCode.InternalServerError)
        }

        assertError(AppError.Unknown) { api.teams() }
        assertEquals(3, calls)
    }

    @Test
    fun status401_isNotRetried() = runTest {
        var calls = 0
        val api = api {
            calls++
            respond("", HttpStatusCode.Unauthorized)
        }

        assertError(AppError.Unauthorized) { api.teams() }
        assertEquals(1, calls)
    }

    @Test
    fun status404_isNotRetried() = runTest {
        var calls = 0
        val api = api {
            calls++
            respond("", HttpStatusCode.NotFound)
        }

        assertError(AppError.Unknown) { api.teams() }
        assertEquals(1, calls)
    }

    @Test
    fun ioException_isNotRetried() = runTest {
        var calls = 0
        val api = api {
            calls++
            throw IOException("offline")
        }

        assertError(AppError.Network) { api.teams() }
        assertEquals(1, calls)
    }

    private suspend fun assertError(expected: AppError, block: suspend () -> Unit) {
        try {
            block()
            fail("Expected AppException($expected)")
        } catch (e: AppException) {
            assertEquals(expected, e.error)
        }
    }
}
