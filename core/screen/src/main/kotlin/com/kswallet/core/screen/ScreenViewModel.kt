package com.kswallet.core.screen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

abstract class ScreenViewModel<S : ScreenState, A : ScreenAction, E : ScreenEvent>(
    initialState: S,
) : ViewModel() {

    private val mutableState = MutableStateFlow(initialState)

    val state: StateFlow<S> = mutableState.asStateFlow()

    private val eventChannel = Channel<E>(
        capacity = Channel.BUFFERED,
    )

    /**
     * Transient output for a single route collector.
     *
     * Not persistent, replayable, or an exactly-once delivery mechanism.
     */
    val events: Flow<E> = eventChannel.receiveAsFlow()

    protected fun updateState(
        transform: (S) -> S,
    ) {
        mutableState.update(transform)
    }

    protected suspend fun sendEvent(event: E) {
        eventChannel.send(event)
    }

    abstract fun onAction(action: A)

    override fun onCleared() {
        eventChannel.close()
        super.onCleared()
    }
}