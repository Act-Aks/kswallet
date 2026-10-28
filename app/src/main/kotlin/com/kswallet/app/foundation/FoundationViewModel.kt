package com.kswallet.app.foundation

import androidx.lifecycle.viewModelScope
import com.kswallet.core.screen.ScreenViewModel
import kotlinx.coroutines.launch

class FoundationViewModel(
    private val counterPolicy: FoundationCounterPolicy,
) : ScreenViewModel<FoundationState, FoundationAction, FoundationEvent>(
    initialState = FoundationState(maximum = counterPolicy.maximum),
) {

    override fun onAction(action: FoundationAction) {
        when (action) {
            FoundationAction.IncrementClicked -> {
                updateState { current ->
                    current.copy(
                        count = counterPolicy.increment(current.count),
                    )
                }
            }

            FoundationAction.ResetClicked -> {
                updateState { current ->
                    current.copy(count = 0)
                }

                viewModelScope.launch {
                    sendEvent(FoundationEvent.ResetCompleted)
                }
            }
        }
    }
}