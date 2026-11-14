package com.voidx.nba.feature.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.voidx.nba.feature.home.view.HomePage
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    navigate(HomeRoute, navOptions)
}

fun NavGraphBuilder.homePage(onTeamClick: (teamId: Int, teamName: String) -> Unit) {
    composable<HomeRoute> {
        HomePage(onTeamClick = onTeamClick)
    }
}
