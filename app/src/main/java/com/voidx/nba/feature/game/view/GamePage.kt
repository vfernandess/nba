package com.voidx.nba.feature.game.view

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.voidx.nba.feature.game.business.GameViewModel
import com.voidx.nba.feature.game.presentation.GameCommand
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamePage(
    onBack: () -> Unit,
    viewModel: GameViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onBack,
        sheetState = sheetState,
    ) {
        GamePageContent(
            state = state,
            onBack = {
                scope.launch { sheetState.hide() }.invokeOnCompletion { onBack() }
            },
            onRetry = { viewModel.dispatch(GameCommand.Retry) },
            onLoadMore = { viewModel.dispatch(GameCommand.LoadMore) },
        )
    }
}
