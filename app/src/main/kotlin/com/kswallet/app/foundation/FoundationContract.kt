package com.kswallet.app.foundation

import com.kswallet.core.screen.ScreenAction
import com.kswallet.core.screen.ScreenEvent
import com.kswallet.core.screen.ScreenState

data class FoundationState(
    val count: Int = 0,
    val maximum: Int = 10,
) : ScreenState {

    val canIncrement: Boolean
        get() = count < maximum
}

sealed interface FoundationAction : ScreenAction {
    data object IncrementClicked : FoundationAction
    data object ResetClicked : FoundationAction
}

sealed interface FoundationEvent : ScreenEvent {
    data object ResetCompleted : FoundationEvent
}