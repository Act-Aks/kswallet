package com.kswallet.core.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

/**
 * Collects [events] while the calling screen is at least [Lifecycle.State.STARTED].
 *
 * Events are forwarded to [onEvent] only while the screen is visible enough to
 * receive them. Collection stops when the lifecycle drops below STARTED and
 * restarts when it returns.
 *
 * This prevents:
 * - events being collected while the screen is in the background;
 * - event collection continuing after the composable leaves composition;
 * - unrelated recompositions from restarting collection.
 *
 * [key1] and [key2] may be supplied when the event source itself changes,
 * such as when a route argument changes.
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
        lifecycleOwner.lifecycle.repeatOnLifecycle(
            Lifecycle.State.STARTED,
        ) {
            events.collect(onEvent)
        }
    }
}