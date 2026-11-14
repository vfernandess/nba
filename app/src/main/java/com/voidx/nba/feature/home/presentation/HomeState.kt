package com.voidx.nba.feature.home.presentation

import androidx.compose.runtime.Stable
import com.voidx.nba.core.ui.ContentState
import com.voidx.nba.domain.model.Team
import com.voidx.nba.domain.model.TeamSort

@Stable
data class HomeState(
    val content: ContentState<List<Team>> = ContentState.Loading,
    val sort: TeamSort = TeamSort.Name,
)
