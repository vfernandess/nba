package com.voidx.nba.feature.home.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
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
import com.voidx.nba.core.ui.ContentState
import com.voidx.nba.core.ui.EmptyReason
import com.voidx.nba.core.ui.StateContent
import com.voidx.nba.core.ui.TableHeader
import com.voidx.nba.core.ui.TableRow
import com.voidx.nba.core.ui.theme.NBATheme
import com.voidx.nba.domain.model.Team
import com.voidx.nba.domain.model.TeamSort
import com.voidx.nba.feature.home.presentation.HomeState

private val TableWeights = listOf(0.45f, 0.30f, 0.25f)

@Composable
fun HomePageContent(
    state: HomeState,
    onSortClick: () -> Unit,
    onTeamClick: (Team) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            HomeTopBar(
                sort = state.sort,
                sortEnabled = state.content !is ContentState.Loading,
                onSortClick = onSortClick,
            )
        },
    ) { innerPadding ->
        StateContent(
            state = state.content,
            onRetry = onRetry,
            emptyMessage = { stringResource(R.string.home_empty) },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) { teams ->
            TeamTable(teams = teams, onTeamClick = onTeamClick)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    sort: TeamSort,
    sortEnabled: Boolean,
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(stringResource(R.string.home_title)) },
        actions = {
            FilledTonalButton(
                onClick = onSortClick,
                enabled = sortEnabled,
                shape = CircleShape,
                modifier = Modifier.padding(end = 8.dp),
            ) {
                Text(stringResource(sort.labelRes()))
                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
            }
        },
    )
}

@Composable
private fun TeamTable(
    teams: List<Team>,
    onTeamClick: (Team) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        TableHeader(
            titles = listOf(
                stringResource(R.string.home_sort_name),
                stringResource(R.string.home_sort_city),
                stringResource(R.string.home_sort_conference),
            ),
            weights = TableWeights,
        )
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items = teams, key = { it.id }) { team ->
                TableRow(
                    cells = listOf(team.fullName, team.city, team.conference),
                    weights = TableWeights,
                    onClick = { onTeamClick(team) },
                )
            }
        }
    }
}

private val previewTeams = listOf(
    Team(id = 1, fullName = "Atlanta Hawks", city = "Atlanta", conference = "East"),
    Team(id = 2, fullName = "Boston Celtics", city = "Boston", conference = "East"),
    Team(id = 14, fullName = "Los Angeles Lakers", city = "Los Angeles", conference = "West"),
)

@Composable
private fun HomePreview(state: HomeState) {
    NBATheme(dynamicColor = false) {
        Surface {
            HomePageContent(state = state, onSortClick = {}, onTeamClick = {}, onRetry = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomePageContentLoadingPreview() {
    HomePreview(HomeState(content = ContentState.Loading))
}

@Preview(showBackground = true)
@Composable
private fun HomePageContentEmptyPreview() {
    HomePreview(HomeState(content = ContentState.Empty(EmptyReason.NoData)))
}

@Preview(showBackground = true)
@Composable
private fun HomePageContentErrorPreview() {
    HomePreview(HomeState(content = ContentState.Error(AppError.Network)))
}

@Preview(showBackground = true)
@Composable
private fun HomePageContentSuccessPreview() {
    HomePreview(HomeState(content = ContentState.Success(previewTeams), sort = TeamSort.City))
}
