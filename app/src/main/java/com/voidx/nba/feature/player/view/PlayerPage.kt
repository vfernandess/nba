package com.voidx.nba.feature.player.view

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.voidx.nba.core.fuse.compose.ext.OnEffect
import com.voidx.nba.feature.player.business.PlayerViewModel
import com.voidx.nba.feature.player.presentation.PlayerCommand
import com.voidx.nba.feature.player.presentation.PlayerSideEffect
import com.voidx.nba.feature.player.presentation.SEARCH_JOB_KEY
import kotlinx.coroutines.flow.drop

@Composable
fun PlayerPage(
    onTeamClick: (teamId: Int, teamName: String) -> Unit,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchState = rememberTextFieldState(initialText = state.query)

    // drop(1): the initial emission is the restored text, so returning to the tab must not re-search.
    LaunchedEffect(searchState) {
        snapshotFlow { searchState.text.toString() }
            .drop(1)
            .collect { viewModel.dispatch(PlayerCommand.Search(it), SEARCH_JOB_KEY) }
    }

    OnEffect(viewModel.sideEffect) { sideEffect ->
        when (sideEffect) {
            is PlayerSideEffect.OpenTeam -> onTeamClick(sideEffect.teamId, sideEffect.teamName)
        }
    }

    PlayerPageContent(
        state = state,
        searchState = searchState,
        onRetry = { viewModel.dispatch(PlayerCommand.Retry, SEARCH_JOB_KEY) },
        onLoadMore = viewModel::onReachedEnd,
        onPlayerClick = { viewModel.dispatch(PlayerCommand.SelectPlayer(it)) },
    )
}
