package com.kswallet.feature.settings.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kswallet.core.navigation.KsWalletDestination
import com.kswallet.feature.settings.presentation.settings.SettingsRoute
import kotlinx.serialization.Serializable

/** Settings is not a tab: it is pushed from the dashboard's top bar. */
@Serializable
data object SettingsDestination : KsWalletDestination

fun NavGraphBuilder.settingsScreen(
    onNavigateBack: () -> Unit,
) {
    composable<SettingsDestination> {
        SettingsRoute(onNavigateBack = onNavigateBack)
    }
}
