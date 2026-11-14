package com.voidx.nba.feature.home.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.voidx.nba.core.fuse.compose.ext.OnEffect
import com.voidx.nba.feature.home.business.HomeViewModel
import com.voidx.nba.feature.home.presentation.HomeCommand
import com.voidx.nba.feature.home.presentation.HomeSideEffect

@Composable
fun HomePage(
    onTeamClick: (teamId: Int, teamName: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showSortDialog by rememberSaveable { mutableStateOf(false) }
    val currentOnTeamClick by rememberUpdatedState(onTeamClick)

    OnEffect(viewModel.sideEffect) { sideEffect ->
        when (sideEffect) {
            is HomeSideEffect.OpenTeam -> currentOnTeamClick(sideEffect.teamId, sideEffect.teamName)
        }
    }

    HomePageContent(
        state = state,
        onSortClick = { showSortDialog = true },
        onTeamClick = { team -> viewModel.dispatch(HomeCommand.SelectTeam(team)) },
        onRetry = { viewModel.dispatch(HomeCommand.Retry) },
        modifier = modifier,
    )

    if (showSortDialog) {
        SortDialog(
            selected = state.sort,
            onSortSelected = { sort ->
                showSortDialog = false
                viewModel.dispatch(HomeCommand.SelectSort(sort))
            },
            onDismiss = { showSortDialog = false },
        )
    }
}
