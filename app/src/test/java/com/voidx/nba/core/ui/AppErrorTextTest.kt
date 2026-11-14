package com.voidx.nba.core.ui

import com.voidx.nba.R
import com.voidx.nba.core.common.AppError
import org.junit.Assert.assertEquals
import org.junit.Test

class AppErrorTextTest {

    @Test
    fun messageRes_mapsEveryError() {
        assertEquals(R.string.error_network, AppError.Network.messageRes())
        assertEquals(R.string.error_rate_limited, AppError.RateLimited.messageRes())
        assertEquals(R.string.error_unauthorized, AppError.Unauthorized.messageRes())
        assertEquals(R.string.error_missing_api_key, AppError.MissingApiKey.messageRes())
        assertEquals(R.string.error_unknown, AppError.Unknown.messageRes())
    }
}
