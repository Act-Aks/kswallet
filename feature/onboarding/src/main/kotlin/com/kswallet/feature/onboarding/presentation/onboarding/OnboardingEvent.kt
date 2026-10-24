package com.kswallet.feature.onboarding.presentation.onboarding

import com.kswallet.core.ui.screen.ScreenEvent
import com.kswallet.core.ui.util.UiMessage

sealed interface OnboardingEvent : ScreenEvent {
    data object Completed : OnboardingEvent
    data class ShowMessage(val message: UiMessage) : OnboardingEvent
}
