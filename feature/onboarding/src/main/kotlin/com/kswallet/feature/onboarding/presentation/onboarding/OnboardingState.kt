package com.kswallet.feature.onboarding.presentation.onboarding

import com.kswallet.core.model.Currency
import com.kswallet.core.ui.screen.ScreenState
import com.kswallet.feature.onboarding.domain.model.OnboardingPreferences

const val ONBOARDING_STEP_COUNT = 3

data class OnboardingState(
    val step: Int = 0,
    val preferences: OnboardingPreferences = OnboardingPreferences(),
    val currencies: List<Currency> = Currency.entries,
    val isSaving: Boolean = false,
) : ScreenState
