package com.kswallet.app.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kswallet.app.navigation.KsWalletNavigationRoot
import com.kswallet.core.datastore.domain.model.ThemeMode
import com.kswallet.core.designsystem.theme.KsWalletTheme
import com.kswallet.feature.settings.domain.usecase.ObserveSettingsUseCase
import org.koin.compose.koinInject

/**
 * Applies persisted settings and displays the appropriate navigation flow.
 *
 * Navigation is composed only after settings load. [onAppReady] notifies
 * the platform host that app content is ready to replace its launch screen.
 *
 * @param onAppReady Idempotent callback invoked after ready content composes.
 * @param observeSettings Use case exposing persisted user settings.
 */
@Composable
fun KsWalletApp(
    onAppReady: () -> Unit = {},
    observeSettings: ObserveSettingsUseCase = koinInject(),
) {
    val settingsFlow = remember(observeSettings) { observeSettings() }
    val settings by settingsFlow.collectAsStateWithLifecycle(initialValue = null)
    // A stable local value that Kotlin can smart-cast.
    val loadedSettings = settings

    KsWalletTheme(
        darkTheme = when (loadedSettings?.themeMode) {
            ThemeMode.SYSTEM, null -> isSystemInDarkTheme()
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
        },
        dynamicColor = loadedSettings?.dynamicColor ?: false,
    ) {
        if (loadedSettings != null) {
            KsWalletNavigationRoot(
                onboardingCompleted = loadedSettings.onboardingCompleted,
            )

            SideEffect {
                onAppReady()
            }
        }
    }
}
