package com.voidx.nba.app

import com.voidx.nba.feature.home.HomeRoute
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TopLevelDestinationTest {

    @Test
    fun navOptions_restoresTabState() {
        val options = TopLevelDestination.Players.navOptions(startRoute = HomeRoute)

        assertTrue(options.shouldRestoreState())
        assertTrue(options.shouldLaunchSingleTop())
        assertTrue(options.shouldPopUpToSaveState())
        assertEquals(HomeRoute::class, options.popUpToRouteClass)
    }
}
