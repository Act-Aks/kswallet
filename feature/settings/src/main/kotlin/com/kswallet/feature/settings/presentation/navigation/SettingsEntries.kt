package com.kswallet.feature.settings.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kswallet.feature.settings.presentation.settings.SettingsRoot

fun EntryProviderScope<NavKey>.settingsEntries(
    onNavigateBack: () -> Unit,
) {
    entry<SettingsRoute> {
        SettingsRoot(onNavigateBack = onNavigateBack)
    }
}
