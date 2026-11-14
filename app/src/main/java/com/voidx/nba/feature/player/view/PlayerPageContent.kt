package com.voidx.nba.feature.player.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.voidx.nba.R
import com.voidx.nba.core.common.AppError
import com.voidx.nba.core.fuse.compose.ext.OnReachedEnd
import com.voidx.nba.core.ui.ContentState
import com.voidx.nba.core.ui.EmptyReason
import com.voidx.nba.core.ui.LoadMoreFooter
import com.voidx.nba.core.ui.StateContent
import com.voidx.nba.core.ui.TableHeader
import com.voidx.nba.core.ui.TableRow
import com.voidx.nba.core.ui.theme.NBATheme
import com.voidx.nba.domain.model.Player
import com.voidx.nba.domain.model.Team
import com.voidx.nba.feature.player.presentation.PlayerState

private val ColumnWeights = listOf(0.3f, 0.3f, 0.4f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerPageContent(
    state: PlayerState,
    searchState: TextFieldState,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onPlayerClick: (Player) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text(stringResource(R.string.player_title)) }) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            OutlinedTextField(
                state = searchState,
                lineLimits = TextFieldLineLimits.SingleLine,
                placeholder = { Text(stringResource(R.string.player_search_placeholder)) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
            StateContent(
                state = state.content,
                onRetry = onRetry,
                emptyMessage = { reason -> emptyMessage(reason) },
            ) { players ->
                PlayerList(
                    players = players,
                    isLoadingMore = state.isLoadingMore,
                    loadMoreError = state.loadMoreError,
                    onLoadMore = onLoadMore,
                    onPlayerClick = onPlayerClick,
                )
            }
        }
    }
}

@Composable
private fun emptyMessage(reason: EmptyReason): String = when (reason) {
    is EmptyReason.NoResults -> stringResource(R.string.player_empty_no_results, reason.query)
    EmptyReason.Prompt, EmptyReason.NoData -> stringResource(R.string.player_empty_prompt)
}

@Composable
private fun PlayerList(
    players: List<Player>,
    isLoadingMore: Boolean,
    loadMoreError: AppError?,
    onLoadMore: () -> Unit,
    onPlayerClick: (Player) -> Unit,
) {
    val listState = rememberLazyListState()
    listState.OnReachedEnd { onLoadMore() }

    Column {
        TableHeader(
            titles = listOf(
                stringResource(R.string.player_header_first_name),
                stringResource(R.string.player_header_last_name),
                stringResource(R.string.player_header_team),
            ),
            weights = ColumnWeights,
        )
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            items(players, key = { it.id }) { player ->
                TableRow(
                    cells = listOf(player.firstName, player.lastName, player.team.fullName),
                    weights = ColumnWeights,
                    onClick = { onPlayerClick(player) },
                )
            }
            item(key = "footer") {
                LoadMoreFooter(isLoading = isLoadingMore, error = loadMoreError, onRetry = onLoadMore)
            }
        }
    }
}

private val PreviewPlayers = listOf(
    Player(1, "LeBron", "James", Team(14, "Los Angeles Lakers", "Los Angeles", "West")),
    Player(2, "Stephen", "Curry", Team(10, "Golden State Warriors", "Golden State", "West")),
    Player(3, "Jayson", "Tatum", Team(2, "Boston Celtics", "Boston", "East")),
)

@Composable
private fun PreviewContent(state: PlayerState) {
    NBATheme(dynamicColor = false) {
        Surface {
            PlayerPageContent(
                state = state,
                searchState = rememberTextFieldState(state.query),
                onRetry = {},
                onLoadMore = {},
                onPlayerClick = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlayerPageContentPromptPreview() {
    PreviewContent(PlayerState())
}

@Preview(showBackground = true)
@Composable
private fun PlayerPageContentLoadingPreview() {
    PreviewContent(PlayerState(query = "le", content = ContentState.Loading))
}

@Preview(showBackground = true)
@Composable
private fun PlayerPageContentResultsPreview() {
    PreviewContent(
        PlayerState(query = "le", content = ContentState.Success(PreviewPlayers), nextCursor = 25, isLoadingMore = true),
    )
}

@Preview(showBackground = true)
@Composable
private fun PlayerPageContentNoResultsPreview() {
    PreviewContent(PlayerState(query = "zzz", content = ContentState.Empty(EmptyReason.NoResults("zzz"))))
}

@Preview(showBackground = true)
@Composable
private fun PlayerPageContentErrorPreview() {
    PreviewContent(PlayerState(query = "le", content = ContentState.Error(AppError.Network)))
}
