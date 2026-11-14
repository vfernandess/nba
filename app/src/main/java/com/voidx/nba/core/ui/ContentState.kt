package com.voidx.nba.core.ui

import com.voidx.nba.core.common.AppError

sealed interface EmptyReason {
    data object NoData : EmptyReason
    data object Prompt : EmptyReason
    data class NoResults(val query: String) : EmptyReason
}

sealed interface ContentState<out T> {
    data object Loading : ContentState<Nothing>
    data class Empty(val reason: EmptyReason) : ContentState<Nothing>
    data class Error(val error: AppError) : ContentState<Nothing>
    data class Success<T>(val data: T) : ContentState<T>
}
