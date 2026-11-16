package com.voidx.nba.feature.game.business

import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

/** Today's date in US Eastern time, the zone the API's game dates are in. */
fun interface Today {

    operator fun invoke(): LocalDate

    class Impl @Inject constructor() : Today {
        override fun invoke(): LocalDate = LocalDate.now(API_ZONE)
    }

    private companion object {
        val API_ZONE: ZoneId = ZoneId.of("America/New_York")
    }
}
