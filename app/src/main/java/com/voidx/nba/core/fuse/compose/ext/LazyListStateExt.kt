package com.voidx.nba.core.fuse.compose.ext

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

private typealias IsScrolling = Boolean
private typealias VisibleIndex = Int

@Composable
fun LazyListState.OnFirstVisibleIndexChanged(
    onVisibleIndexChanged: (VisibleIndex) -> Unit
) {
    LaunchedEffect(key1 = this) {
        snapshotFlow { this@OnFirstVisibleIndexChanged.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect(onVisibleIndexChanged)
    }
}

@Composable
fun LazyListState.OnScrollStateChanged(
    onScrollStateChanged: (IsScrolling) -> Unit
) {
    LaunchedEffect(key1 = this) {
        snapshotFlow { this@OnScrollStateChanged.isScrollInProgress }
            .distinctUntilChanged()
            .collect(onScrollStateChanged)
    }
}

internal fun shouldLoadMore(lastVisibleIndex: Int, totalItems: Int, buffer: Int): Boolean =
    totalItems > 0 && lastVisibleIndex >= totalItems - 1 - buffer

internal data class ListEnd(val totalItems: Int, val atEnd: Boolean)

/**
 * Emits the item count each time the list is at its end: on arriving there, and again when the item
 * count changes while it stays there (e.g. a short page was appended), so paging never stalls.
 */
internal fun Flow<ListEnd>.reachedEndSignals(): Flow<Int> =
    distinctUntilChanged()
        .filter { it.atEnd }
        .map { it.totalItems }

@Composable
fun LazyListState.OnReachedEnd(
    buffer: Int = 5,
    onReachedEnd: () -> Unit
) {
    LaunchedEffect(key1 = this) {
        snapshotFlow {
            val totalItems = layoutInfo.totalItemsCount
            ListEnd(
                totalItems = totalItems,
                atEnd = shouldLoadMore(
                    lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1,
                    totalItems = totalItems,
                    buffer = buffer
                )
            )
        }
            .reachedEndSignals()
            .collect { onReachedEnd() }
    }
}
