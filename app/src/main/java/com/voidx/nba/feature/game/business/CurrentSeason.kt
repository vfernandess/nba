package com.voidx.nba.feature.game.business

import java.time.LocalDate
import java.time.Month
import javax.inject.Inject

fun seasonFor(date: LocalDate): Int =
    if (date.month >= Month.OCTOBER) date.year else date.year - 1

interface CurrentSeason {

    operator fun invoke(): Int

    class Impl @Inject constructor() : CurrentSeason {
        override fun invoke(): Int = seasonFor(LocalDate.now())
    }
}
