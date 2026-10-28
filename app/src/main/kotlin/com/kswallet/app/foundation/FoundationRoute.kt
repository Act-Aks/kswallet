package com.kswallet.app.foundation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FoundationRoute(
    viewModel: FoundationViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current

    var resetNoticeVisible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(
            Lifecycle.State.STARTED,
        ) {
            viewModel.events.collectLatest { event ->
                when (event) {
                    FoundationEvent.ResetCompleted -> {
                        resetNoticeVisible = true

                        try {
                            delay(2_000)
                        } finally {
                            resetNoticeVisible = false
                        }
                    }
                }
            }
        }
    }

    FoundationScreen(
        state = state,
        resetNoticeVisible = resetNoticeVisible,
        onAction = viewModel::onAction,
    )
}