package com.voidx.nba.data.remote

import com.voidx.nba.core.common.AppError
import com.voidx.nba.core.common.AppException
import com.voidx.nba.core.network.KeyProvider
import com.voidx.nba.data.remote.dto.GameDto
import com.voidx.nba.data.remote.dto.PageDto
import com.voidx.nba.data.remote.dto.PlayerDto
import com.voidx.nba.data.remote.dto.TeamDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import java.io.IOException
import javax.inject.Inject

class BallDontLieApi @Inject constructor(
    private val client: HttpClient,
    private val keys: KeyProvider,
) {
    suspend fun teams(): PageDto<TeamDto> = safeCall {
        client.get("teams").body()
    }

    suspend fun games(teamId: Int, season: Int, cursor: Int?, perPage: Int): PageDto<GameDto> = safeCall {
        client.get("games") {
            parameter("team_ids[]", teamId)
            parameter("seasons[]", season)
            cursor?.let { parameter("cursor", it) }
            parameter("per_page", perPage)
        }.body()
    }

    suspend fun players(search: String, cursor: Int?, perPage: Int): PageDto<PlayerDto> = safeCall {
        client.get("players") {
            parameter("search", search)
            cursor?.let { parameter("cursor", it) }
            parameter("per_page", perPage)
        }.body()
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun <T> safeCall(block: suspend () -> T): T {
        if (keys.apiKey().isBlank()) throw AppException(AppError.MissingApiKey)
        return try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: AppException) {
            throw e
        } catch (e: ClientRequestException) {
            throw AppException(e.toAppError(), e)
        } catch (e: ServerResponseException) {
            throw AppException(AppError.Unknown, e)
        } catch (e: IOException) {
            throw AppException(AppError.Network, e)
        } catch (e: Exception) {
            throw AppException(AppError.Unknown, e)
        }
    }

    private fun ClientRequestException.toAppError(): AppError = when (response.status) {
        HttpStatusCode.Unauthorized -> AppError.Unauthorized
        HttpStatusCode.TooManyRequests -> AppError.RateLimited
        else -> AppError.Unknown
    }
}
