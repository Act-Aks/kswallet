package com.kswallet.core.datastore.domain.model

import com.kswallet.core.model.Currency

data class UserSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = false,
    val defaultCurrency: Currency = Currency.Default,
    val onboardingCompleted: Boolean = false,
)
