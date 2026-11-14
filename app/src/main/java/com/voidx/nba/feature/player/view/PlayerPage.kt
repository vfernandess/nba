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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.withIndex

@Composable
fun PlayerPage(
    onTeamClick: (teamId: Int, teamName: String) -> Unit,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchState = rememberTextFieldState(initialText = state.query)

    LaunchedEffect(searchState) {
        snapshotFlow { searchState.text.toString() }
            .searchesToDispatch { viewModel.state.value.query }
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

/**
 * The first emission is the restored field text: skip it when the ViewModel already holds it
 * (rotation, tab return) but search it after process death, where the new ViewModel's query is empty.
 * Later edits always search.
 */
internal fun Flow<String>.searchesToDispatch(currentQuery: () -> String): Flow<String> =
    withIndex()
        .filter { (index, text) -> index > 0 || text != currentQuery() }
        .map { it.value }
