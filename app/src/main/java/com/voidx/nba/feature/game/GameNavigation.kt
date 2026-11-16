package com.voidx.nba.feature.game

import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import com.voidx.nba.feature.game.view.GamePage
import kotlinx.serialization.Serializable


@Serializable
data class GameRoute(val teamId: Int, val teamName: String)

fun NavGraphBuilder.gameSheet(onDismiss: () -> Unit) {
    dialog<GameRoute>(
        dialogProperties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        GamePage(onBack = onDismiss)
    }
}

fun NavController.navigateToGame(teamId: Int, teamName: String) {
    navigate(GameRoute(teamId, teamName)) {
        launchSingleTop = true
    }
}
