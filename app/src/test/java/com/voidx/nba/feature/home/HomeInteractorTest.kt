package com.voidx.nba.feature.home

import app.cash.turbine.test
import com.voidx.nba.core.common.AppError
import com.voidx.nba.core.ui.ContentState
import com.voidx.nba.domain.model.TeamSort
import com.voidx.nba.feature.home.business.GetTeamsUseCase
import com.voidx.nba.feature.home.business.SortTeamsUseCase
import com.voidx.nba.feature.home.presentation.HomeCommand
import com.voidx.nba.feature.home.presentation.HomeEffect
import com.voidx.nba.feature.home.presentation.HomeInteractor
import com.voidx.nba.feature.home.presentation.HomeSideEffect
import com.voidx.nba.feature.home.presentation.HomeState
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeInteractorTest {

    private val repository = FakeTeamRepository(teams = unsortedTeams)
    private val sortTeams = SortTeamsUseCase.Impl()
    private val interactor: HomeInteractor =
        HomeInteractor.Impl(GetTeamsUseCase.Impl(repository, sortTeams), sortTeams)

    private val loadedState = HomeState(content = ContentState.Success(sortTeams(unsortedTeams, TeamSort.Name)))

    @Test
    fun selectSort_withLoadedTeams_emitsSortChangedWithResortedTeams() = runTest {
        val effects = interactor(loadedState, HomeCommand.SelectSort(TeamSort.City)).toList()

        assertEquals(
            listOf<HomeEffect>(
                HomeEffect.SortChanged(TeamSort.City, listOf(hawks, celtics, bulls, lakers)),
            ),
            effects,
        )
    }

    @Test
    fun selectSort_whileLoading_emitsSortChangedWithoutTeams() = runTest {
        val effects = interactor(HomeState(), HomeCommand.SelectSort(TeamSort.City)).toList()

        assertEquals(listOf<HomeEffect>(HomeEffect.SortChanged(TeamSort.City, null)), effects)
    }

    @Test
    fun selectTeam_emitsOpenTeamSideEffect() = runTest {
        interactor.sideEffect.test {
            interactor(loadedState, HomeCommand.SelectTeam(hawks)).toList()

            assertEquals(HomeSideEffect.OpenTeam(1, "Atlanta Hawks"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun load_whenNotLoaded_loadsTeamsWithCurrentSort() = runTest {
        val effects = interactor(HomeState(sort = TeamSort.City), HomeCommand.Load).toList()

        assertEquals(
            listOf(
                HomeEffect.Loading,
                HomeEffect.ShowTeams(listOf(hawks, celtics, bulls, lakers)),
            ),
            effects,
        )
        assertEquals(1, repository.callCount)
    }

    @Test
    fun load_ignoredWhenAlreadyLoaded() = runTest {
        val effects = interactor(loadedState, HomeCommand.Load).toList()

        assertEquals(emptyList<HomeEffect>(), effects)
        assertEquals(0, repository.callCount)
    }

    @Test
    fun retry_alwaysReloads() = runTest {
        val effects = interactor(loadedState, HomeCommand.Retry).toList()

        assertEquals(HomeEffect.Loading, effects.first())
        assertEquals(1, repository.callCount)
    }

    @Test
    fun retry_afterError_reloads() = runTest {
        val errorState = HomeState(content = ContentState.Error(AppError.Network))

        val effects = interactor(errorState, HomeCommand.Retry).toList()

        assertEquals(HomeEffect.Loading, effects.first())
        assertEquals(1, repository.callCount)
    }
}
