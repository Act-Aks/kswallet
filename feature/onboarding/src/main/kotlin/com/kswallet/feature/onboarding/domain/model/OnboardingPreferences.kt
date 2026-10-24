package com.kswallet.feature.onboarding.domain.model

import com.kswallet.core.datastore.domain.model.ThemeMode
import com.kswallet.core.model.Currency

/** Preferences collected once before the wallet's first use. */
data class OnboardingPreferences(
    val defaultCurrency: Currency = Currency.Default,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = false,
)
