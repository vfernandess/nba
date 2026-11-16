package com.voidx.nba.feature.home

import app.cash.turbine.test
import com.voidx.nba.core.common.AppError
import com.voidx.nba.core.common.AppException
import com.voidx.nba.domain.model.Team
import com.voidx.nba.domain.model.TeamSort
import com.voidx.nba.feature.home.business.GetTeamsUseCase
import com.voidx.nba.feature.home.business.SortTeamsUseCase
import com.voidx.nba.feature.home.presentation.HomeEffect
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetTeamsUseCaseTest {

    private val repository = FakeTeamRepository()
    private val useCase: GetTeamsUseCase = GetTeamsUseCase.Impl(repository, SortTeamsUseCase.Impl())

    @Test
    fun invoke_success_emitsLoadingThenSortedTeams() = runTest {
        repository.teams = unsortedTeams

        useCase(TeamSort.City).test {
            assertEquals(HomeEffect.Loading, awaitItem())
            assertEquals(
                HomeEffect.ShowTeams(listOf(hawks, celtics, bulls, lakers)),
                awaitItem(),
            )
            awaitComplete()
        }
    }

    @Test
    fun invoke_filtersTeamsWithBlankConference() = runTest {
        val noConference = Team(id = 50, fullName = "All-Stars", name = "Stars", city = "Nowhere", conference = " ")
        repository.teams = listOf(noConference, hawks)

        useCase(TeamSort.Name).test {
            assertEquals(HomeEffect.Loading, awaitItem())
            assertEquals(HomeEffect.ShowTeams(listOf(hawks)), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun invoke_whenAllFiltered_emitsShowEmpty() = runTest {
        repository.teams = listOf(Team(id = 50, fullName = "All-Stars", name = "Stars", city = "Nowhere", conference = " "))

        useCase(TeamSort.Name).test {
            assertEquals(HomeEffect.Loading, awaitItem())
            assertEquals(HomeEffect.ShowEmpty, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun invoke_whenRepositoryReturnsNothing_emitsShowEmpty() = runTest {
        repository.teams = emptyList()

        useCase(TeamSort.Name).test {
            assertEquals(HomeEffect.Loading, awaitItem())
            assertEquals(HomeEffect.ShowEmpty, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun invoke_whenRateLimited_emitsShowError() = runTest {
        repository.failure = AppException(AppError.RateLimited)

        useCase(TeamSort.Name).test {
            assertEquals(HomeEffect.Loading, awaitItem())
            assertEquals(HomeEffect.ShowError(AppError.RateLimited), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun invoke_whenUnexpectedFailure_emitsUnknownError() = runTest {
        repository.failure = IllegalStateException("boom")

        useCase(TeamSort.Name).test {
            assertEquals(HomeEffect.Loading, awaitItem())
            assertEquals(HomeEffect.ShowError(AppError.Unknown), awaitItem())
            awaitComplete()
        }
    }
}
