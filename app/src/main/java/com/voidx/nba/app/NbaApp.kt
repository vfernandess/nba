package com.voidx.nba.app

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.voidx.nba.feature.home.HomeRoute
import com.voidx.nba.feature.home.navigateToHome
import com.voidx.nba.feature.player.navigateToPlayer

@Composable
fun NbaApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                TopLevelDestination.entries.forEach { destination ->
                    val selected = currentDestination?.hierarchy
                        ?.any { it.hasRoute(destination.routeClass) } == true
                    val label = stringResource(destination.label)
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            val options = destination.navOptions(startRoute = HomeRoute)
                            when (destination) {
                                TopLevelDestination.Home -> navController.navigateToHome(options)
                                TopLevelDestination.Players -> navController.navigateToPlayer(options)
                            }
                        },
                        icon = { Icon(imageVector = destination.icon, contentDescription = null) },
                        label = { Text(text = label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        RootNavigation(
            navController = navController,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        )
    }
}
