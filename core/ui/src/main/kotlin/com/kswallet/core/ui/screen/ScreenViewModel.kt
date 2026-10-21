package com.kswallet.core.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * The presentation-layer contract used by every KsWallet screen.
 *
 * ```
 * User ---- Action ---->  ViewModel  ---- State (StateFlow)  ----> UI
 *                            |
 *                            +---------- Event (Channel) ------> one-shot UI events
 * ```
 *
 * Rules the app relies on:
 *  - state is a single immutable object, so a screen is a pure function of it;
 *  - events are delivered exactly once (a Channel, not a SharedFlow);
 *  - the ViewModel owns the whole lifecycle of its work: `viewModelScope` cancels it.
 */
abstract class ScreenViewModel<S : ScreenState, A : ScreenAction, E : ScreenEvent>(
    initialState: S,
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)

    /** What the UI renders. Never mutated from outside the ViewModel. */
    val state: StateFlow<S> = _state.asStateFlow()

    private val _events = Channel<E>(capacity = Channel.BUFFERED)

    /** One-shot events. Collected with `ObserveAsEvents` so they cannot be replayed. */
    val events: Flow<E> = _events.receiveAsFlow()

    /** Current state, for imperative reads inside `onAction`. */
    protected val currentState: S get() = _state.value

    /** Reduces the current state into the next one. */
    protected fun setState(reducer: S.() -> S) {
        _state.update(reducer)
    }

    /** Emits a one-shot event. Safe to call from any coroutine of the ViewModel. */
    protected fun sendEvent(event: E) {
        viewModelScope.launch {
            _events.send(event)
        }
    }

    /** Single entry point for everything the UI asks for. */
    abstract fun onAction(action: A)
}
