package com.voidx.nba.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.voidx.nba.R

@Composable
fun <T> StateContent(
    state: ContentState<T>,
    onRetry: () -> Unit,
    emptyMessage: @Composable (EmptyReason) -> String,
    modifier: Modifier = Modifier,
    success: @Composable (T) -> Unit,
) {
    when (state) {
        ContentState.Loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        is ContentState.Empty -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = emptyMessage(state.reason),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp),
            )
        }

        is ContentState.Error -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(16.dp),
            ) {
                Text(text = stringResource(state.error.messageRes()), textAlign = TextAlign.Center)
                Button(onClick = onRetry) { Text(stringResource(R.string.action_retry)) }
            }
        }

        is ContentState.Success -> Box(modifier) { success(state.data) }
    }
}
