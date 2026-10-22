package com.kswallet.feature.settings.presentation.settings

import com.kswallet.core.datastore.domain.model.UserSettings
import com.kswallet.core.model.Currency
import com.kswallet.core.ui.screen.ScreenState

data class SettingsState(
    val isLoading: Boolean = true,
    val settings: UserSettings = UserSettings(),
    /** Every currency the app knows, so the screen renders a list and not a lookup. */
    val currencies: List<Currency> = Currency.entries,
) : ScreenState
