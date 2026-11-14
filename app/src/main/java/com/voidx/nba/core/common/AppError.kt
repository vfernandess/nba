package com.voidx.nba.core.common

sealed interface AppError {
    data object Network : AppError
    data object RateLimited : AppError
    data object Unauthorized : AppError
    data object MissingApiKey : AppError
    data object Unknown : AppError
}

class AppException(val error: AppError, cause: Throwable? = null) : Exception(cause)
