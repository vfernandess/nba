package com.voidx.nba.feature.game

import com.voidx.nba.feature.game.business.seasonFor
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class CurrentSeasonTest {

    @Test
    fun seasonFor_novemberBelongsToSameYear() {
        assertEquals(2026, seasonFor(LocalDate.of(2026, 11, 14)))
    }

    @Test
    fun seasonFor_firstOfOctoberStartsNewSeason() {
        assertEquals(2026, seasonFor(LocalDate.of(2026, 10, 1)))
    }

    @Test
    fun seasonFor_endOfSeptemberBelongsToPreviousSeason() {
        assertEquals(2025, seasonFor(LocalDate.of(2026, 9, 30)))
    }

    @Test
    fun seasonFor_marchBelongsToPreviousYearSeason() {
        assertEquals(2026, seasonFor(LocalDate.of(2027, 3, 1)))
    }
}
