package com.kswallet.feature.settings.presentation.settings

import com.kswallet.core.datastore.domain.model.ThemeMode
import com.kswallet.core.model.Currency
import com.kswallet.core.ui.screen.ScreenAction

sealed interface SettingsAction : ScreenAction {
    data class ThemeSelected(val mode: ThemeMode) : SettingsAction
    data class DynamicColorToggled(val enabled: Boolean) : SettingsAction
    data class CurrencySelected(val currency: Currency) : SettingsAction
    data object BackClicked : SettingsAction
}
