package com.kswallet.feature.settings.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kswallet.core.navigation.KsWalletDestination
import com.kswallet.feature.settings.presentation.settings.SettingsRoute
import kotlinx.serialization.Serializable

/** Settings is not a tab: it is pushed from the dashboard's top bar. */
@Serializable
data object SettingsDestination : KsWalletDestination

fun EntryProviderScope<NavKey>.settingsEntries(
    onNavigateBack: () -> Unit,
) {
    entry<SettingsDestination> {
        SettingsRoute(onNavigateBack = onNavigateBack)
    }
}
