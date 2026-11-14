package com.voidx.nba.feature.player

import com.voidx.nba.core.common.AppError
import com.voidx.nba.core.common.AppException
import com.voidx.nba.domain.model.Page
import com.voidx.nba.feature.player.business.SearchPlayersUseCase
import com.voidx.nba.feature.player.presentation.PlayerEffect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchPlayersUseCaseTest {

    private val repository = FakePlayerRepository()
    private val useCase = SearchPlayersUseCase.Impl(repository)

    @Test
    fun firstPage_emitsLoadingThenShowPage() = runTest {
        val players = listOf(testPlayer(1), testPlayer(2))
        repository.handler = { _, _, _ -> Page(players, nextCursor = 25) }

        val effects = useCase("lebron", cursor = null).toList()

        assertEquals(
            listOf(PlayerEffect.Loading, PlayerEffect.ShowPage(players, 25, append = false)),
            effects,
        )
        assertEquals(listOf(FakePlayerRepository.Call("lebron", null, 25)), repository.calls)
    }

    @Test
    fun withCursor_emitsLoadingMoreThenAppendedPage() = runTest {
        val players = listOf(testPlayer(3))
        repository.handler = { _, _, _ -> Page(players, nextCursor = null) }

        val effects = useCase("lebron", cursor = 25).toList()

        assertEquals(
            listOf(PlayerEffect.LoadingMore, PlayerEffect.ShowPage(players, null, append = true)),
            effects,
        )
        assertEquals(listOf(FakePlayerRepository.Call("lebron", 25, 25)), repository.calls)
    }

    @Test
    fun emptyFirstPage_emitsNoResults() = runTest {
        repository.handler = { _, _, _ -> Page(emptyList(), nextCursor = null) }

        val effects = useCase("zzz", cursor = null).toList()

        assertEquals(listOf(PlayerEffect.Loading, PlayerEffect.ShowNoResults("zzz")), effects)
    }

    @Test
    fun errorOnFirstPage_emitsShowError() = runTest {
        repository.handler = { _, _, _ -> throw AppException(AppError.Network) }

        val effects = useCase("lebron", cursor = null).toList()

        assertEquals(listOf(PlayerEffect.Loading, PlayerEffect.ShowError(AppError.Network)), effects)
    }

    @Test
    fun errorWithCursor_emitsShowLoadMoreError() = runTest {
        repository.handler = { _, _, _ -> throw AppException(AppError.RateLimited) }

        val effects = useCase("lebron", cursor = 25).toList()

        assertEquals(
            listOf(PlayerEffect.LoadingMore, PlayerEffect.ShowLoadMoreError(AppError.RateLimited)),
            effects,
        )
    }

    @Test
    fun unexpectedException_mapsToUnknown() = runTest {
        repository.handler = { _, _, _ -> error("boom") }

        val effects = useCase("lebron", cursor = null).toList()

        assertEquals(listOf(PlayerEffect.Loading, PlayerEffect.ShowError(AppError.Unknown)), effects)
    }
}
