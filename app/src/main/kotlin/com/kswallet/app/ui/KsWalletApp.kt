package com.kswallet.app.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kswallet.app.navigation.KsWalletNavigationRoot
import com.kswallet.core.datastore.domain.model.ThemeMode
import com.kswallet.core.datastore.domain.model.UserSettings
import com.kswallet.core.designsystem.theme.KsWalletTheme
import com.kswallet.feature.settings.domain.usecase.ObserveSettingsUseCase
import org.koin.compose.koinInject

/**
 * The app shell: theme, bottom navigation, and Navigation 3 state.
 *
 * Each top-level tab owns a savable back stack so switching tabs retains that tab's place.
 * Screens pushed above a tab root hide the bottom bar; system back pops that stack, then
 * returns to Home when a non-Home stack reaches its root.
 */
@Composable
fun KsWalletApp(
    observeSettings: ObserveSettingsUseCase = koinInject(),
) {
    val settings by observeSettings().collectAsStateWithLifecycle(initialValue = UserSettings())

    KsWalletTheme(
        darkTheme = when (settings.themeMode) {
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
        },
        dynamicColor = settings.dynamicColor,
    ) {
        KsWalletNavigationRoot()
    }
}
