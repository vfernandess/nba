package com.voidx.nba.feature.player

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.voidx.nba.feature.player.view.PlayerPage
import kotlinx.serialization.Serializable

@Serializable
data object PlayerRoute

fun NavGraphBuilder.playerPage(onTeamClick: (teamId: Int, teamName: String) -> Unit) {
    composable<PlayerRoute> {
        PlayerPage(onTeamClick = onTeamClick)
    }
}

fun NavController.navigateToPlayer(navOptions: NavOptions? = null) {
    navigate(PlayerRoute, navOptions)
}
