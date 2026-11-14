package com.voidx.nba.feature.home

import com.voidx.nba.core.common.AppError
import com.voidx.nba.core.ui.ContentState
import com.voidx.nba.core.ui.EmptyReason
import com.voidx.nba.domain.model.TeamSort
import com.voidx.nba.feature.home.presentation.HomeEffect
import com.voidx.nba.feature.home.presentation.HomeReducer
import com.voidx.nba.feature.home.presentation.HomeState
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeReducerTest {

    private val reducer = HomeReducer.Impl()

    private fun reduce(effect: HomeEffect, state: HomeState = HomeState()) =
        reducer.reduce(effect, state)

    @Test
    fun initialState_isLoadingSortedByName() {
        assertEquals(HomeState(ContentState.Loading, TeamSort.Name), reducer.state.value)
    }

    @Test
    fun loading_setsLoadingContent() {
        val state = HomeState(content = ContentState.Success(listOf(hawks)))

        assertEquals(ContentState.Loading, reduce(HomeEffect.Loading, state).content)
    }

    @Test
    fun showTeams_setsSuccess() {
        val result = reduce(HomeEffect.ShowTeams(listOf(hawks)))

        assertEquals(ContentState.Success(listOf(hawks)), result.content)
    }

    @Test
    fun showEmpty_setsEmptyNoData() {
        assertEquals(ContentState.Empty(EmptyReason.NoData), reduce(HomeEffect.ShowEmpty).content)
    }

    @Test
    fun showError_setsError() {
        val result = reduce(HomeEffect.ShowError(AppError.Network))

        assertEquals(ContentState.Error(AppError.Network), result.content)
    }

    @Test
    fun sortChanged_withTeams_setsSortAndSuccess() {
        val result = reduce(HomeEffect.SortChanged(TeamSort.City, listOf(hawks, celtics)))

        assertEquals(TeamSort.City, result.sort)
        assertEquals(ContentState.Success(listOf(hawks, celtics)), result.content)
    }

    @Test
    fun sortChanged_withoutTeams_onlyChangesSort() {
        val state = HomeState(content = ContentState.Error(AppError.Network))

        val result = reduce(HomeEffect.SortChanged(TeamSort.City, null), state)

        assertEquals(HomeState(ContentState.Error(AppError.Network), TeamSort.City), result)
    }

    @Test
    fun teamSelected_leavesStateUnchanged() {
        val state = HomeState(content = ContentState.Success(listOf(hawks)), sort = TeamSort.City)

        assertEquals(state, reduce(HomeEffect.TeamSelected(hawks), state))
    }
}
