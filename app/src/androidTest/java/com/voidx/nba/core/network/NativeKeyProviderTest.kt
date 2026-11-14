package com.voidx.nba.core.network

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NativeKeyProviderTest {

    @Test
    fun baseUrl_isCompiledIn() {
        assertEquals("https://api.balldontlie.io/v1/", NativeKeyProvider().baseUrl())
    }

    @Test
    fun apiKey_isNotNull() {
        assertNotNull(NativeKeyProvider().apiKey())
    }
}
