package com.voidx.nba.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.voidx.nba.feature.game.GameOrigin
import com.voidx.nba.feature.game.GameRoute
import com.voidx.nba.feature.game.gameSheet
import com.voidx.nba.feature.game.navigateToGame
import com.voidx.nba.feature.home.HomeRoute
import com.voidx.nba.feature.home.homePage
import com.voidx.nba.feature.player.playerPage

@Composable
fun RootNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier,
    ) {
        homePage { teamId, teamName ->
            navController.navigateToGame(teamId, teamName, GameOrigin.Home)
        }
        playerPage { teamId, teamName ->
            navController.navigateToGame(teamId, teamName, GameOrigin.Player)
        }
        // Pops only the sheet: a repeated dismiss (e.g. double tap) is a no-op
        // instead of popping the tab underneath.
        gameSheet(onDismiss = { navController.popBackStack<GameRoute>(inclusive = true) })
    }
}
