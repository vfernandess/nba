package com.voidx.nba.core.fuse.compose.ext

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

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

@Composable
fun LazyListState.OnReachedEnd(
    buffer: Int = 5,
    onReachedEnd: () -> Unit
) {
    LaunchedEffect(key1 = this) {
        snapshotFlow {
            shouldLoadMore(
                lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1,
                totalItems = layoutInfo.totalItemsCount,
                buffer = buffer
            )
        }
            .distinctUntilChanged()
            .filter { it }
            .collect { onReachedEnd() }
    }
}
