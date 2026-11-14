package com.voidx.nba.feature.game.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import com.voidx.nba.domain.model.Game
import com.voidx.nba.feature.game.GameOrigin
import com.voidx.nba.feature.game.presentation.GameState

private val ColumnWeights = listOf(0.32f, 0.18f, 0.32f, 0.18f)

@Composable
fun GamePageContent(
    state: GameState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        GameHeader(teamName = state.teamName, origin = state.origin, onBack = onBack)
        StateContent(
            state = state.content,
            onRetry = onRetry,
            emptyMessage = { stringResource(R.string.game_empty) },
        ) { games ->
            GameList(
                games = games,
                isLoadingMore = state.isLoadingMore,
                loadMoreError = state.loadMoreError,
                onLoadMore = onLoadMore,
            )
        }
    }
}

@Composable
private fun GameHeader(teamName: String, origin: GameOrigin, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        TextButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
            Text(
                stringResource(
                    if (origin == GameOrigin.Player) R.string.game_back_players else R.string.game_back_home
                )
            )
        }
        Text(
            text = teamName,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 96.dp),
        )
    }
}

@Composable
private fun GameList(
    games: List<Game>,
    isLoadingMore: Boolean,
    loadMoreError: AppError?,
    onLoadMore: () -> Unit,
) {
    val listState = rememberLazyListState()
    listState.OnReachedEnd { onLoadMore() }

    Column(modifier = Modifier.fillMaxSize()) {
        TableHeader(
            titles = listOf(
                stringResource(R.string.game_header_home_name),
                stringResource(R.string.game_header_home_score),
                stringResource(R.string.game_header_visitor_name),
                stringResource(R.string.game_header_visitor_score),
            ),
            weights = ColumnWeights,
        )
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            items(games, key = { it.id }) { game ->
                val pending = stringResource(R.string.game_score_pending)
                TableRow(
                    cells = listOf(
                        game.homeTeamName,
                        if (game.isFinal) game.homeTeamScore.toString() else pending,
                        game.visitorTeamName,
                        if (game.isFinal) game.visitorTeamScore.toString() else pending,
                    ),
                    weights = ColumnWeights,
                    onClick = null,
                )
            }
            item {
                LoadMoreFooter(
                    isLoading = isLoadingMore,
                    error = loadMoreError,
                    onRetry = onLoadMore,
                )
            }
        }
    }
}

private val PreviewGames = listOf(
    Game(1, "Atlanta Hawks", 112, "Boston Celtics", 108, isFinal = true),
    Game(2, "Boston Celtics", 0, "Atlanta Hawks", 0, isFinal = false),
    Game(3, "Atlanta Hawks", 99, "Miami Heat", 101, isFinal = true),
)

private val PreviewState = GameState(
    teamId = 1,
    teamName = "Atlanta Hawks",
    origin = GameOrigin.Home,
    content = ContentState.Success(PreviewGames),
)

@Composable
private fun PreviewHost(state: GameState) {
    NBATheme {
        Surface {
            GamePageContent(state = state, onBack = {}, onRetry = {}, onLoadMore = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GamePageContentLoadingPreview() {
    PreviewHost(PreviewState.copy(content = ContentState.Loading))
}

@Preview(showBackground = true)
@Composable
private fun GamePageContentEmptyPreview() {
    PreviewHost(PreviewState.copy(content = ContentState.Empty(EmptyReason.NoData), origin = GameOrigin.Player))
}

@Preview(showBackground = true)
@Composable
private fun GamePageContentErrorPreview() {
    PreviewHost(PreviewState.copy(content = ContentState.Error(AppError.Network)))
}

@Preview(showBackground = true)
@Composable
private fun GamePageContentSuccessPreview() {
    PreviewHost(PreviewState.copy(isLoadingMore = true))
}
