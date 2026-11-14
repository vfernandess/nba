package com.voidx.nba.core.ui

import androidx.annotation.StringRes
import com.voidx.nba.R
import com.voidx.nba.core.common.AppError

@StringRes
fun AppError.messageRes(): Int = when (this) {
    AppError.Network -> R.string.error_network
    AppError.RateLimited -> R.string.error_rate_limited
    AppError.Unauthorized -> R.string.error_unauthorized
    AppError.MissingApiKey -> R.string.error_missing_api_key
    AppError.Unknown -> R.string.error_unknown
}
