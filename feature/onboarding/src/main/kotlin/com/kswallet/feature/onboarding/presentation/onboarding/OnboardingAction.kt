package com.kswallet.feature.onboarding.presentation.onboarding

import com.kswallet.core.datastore.domain.model.ThemeMode
import com.kswallet.core.model.Currency
import com.kswallet.core.ui.screen.ScreenAction

sealed interface OnboardingAction : ScreenAction {
    data object ContinueClicked : OnboardingAction
    data object BackClicked : OnboardingAction
    data class CurrencySelected(val currency: Currency) : OnboardingAction
    data class ThemeSelected(val mode: ThemeMode) : OnboardingAction
    data class DynamicColorToggled(val enabled: Boolean) : OnboardingAction
    data object FinishClicked : OnboardingAction
}
