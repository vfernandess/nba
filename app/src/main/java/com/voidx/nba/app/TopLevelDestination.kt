package com.voidx.nba.app

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavOptions
import androidx.navigation.navOptions as buildNavOptions
import com.voidx.nba.R
import com.voidx.nba.feature.home.HomeRoute
import com.voidx.nba.feature.player.PlayerRoute
import kotlin.reflect.KClass

enum class TopLevelDestination(@param:StringRes val label: Int, val icon: ImageVector) {
    Home(R.string.app_nav_home, Icons.Filled.Home),
    Players(R.string.app_nav_players, Icons.Filled.Person),
}

internal val TopLevelDestination.routeClass: KClass<*>
    get() = when (this) {
        TopLevelDestination.Home -> HomeRoute::class
        TopLevelDestination.Players -> PlayerRoute::class
    }

fun TopLevelDestination.navOptions(startRoute: Any): NavOptions = buildNavOptions {
    popUpTo(startRoute::class) { saveState = true }
    launchSingleTop = true
    restoreState = true
}
