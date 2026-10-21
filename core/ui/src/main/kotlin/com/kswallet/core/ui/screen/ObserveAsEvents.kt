package com.kswallet.core.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

/**
 * Collects [events] while the screen is at least STARTED and forwards them to [onEvent].
 *
 * Using the lifecycle owner (instead of a plain `LaunchedEffect(Unit)`) is what stops
 * events from being re-delivered after a configuration change, and what stops them from
 * firing while the screen is in the background.
 */
@Composable
fun <T> ObserveAsEvents(
    events: Flow<T>,
    key1: Any? = null,
    key2: Any? = null,
    onEvent: (T) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner, events, key1, key2) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            events.collect(onEvent)
        }
    }
}
