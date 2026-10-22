package com.kswallet.app.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kswallet.app.navigation.KsWalletNavHost
import com.kswallet.app.navigation.KsWalletTopLevelDestination
import com.kswallet.app.navigation.navigateTopLevelTo
import com.kswallet.core.datastore.domain.model.ThemeMode
import com.kswallet.core.datastore.domain.model.UserSettings
import com.kswallet.core.designsystem.theme.KsWalletTheme
import com.kswallet.core.navigation.isOnDestination
import com.kswallet.feature.settings.domain.usecase.ObserveSettingsUseCase
import org.koin.compose.koinInject

/**
 * The app shell: theme, bottom navigation and the navigation graph.
 *
 * The bottom bar appears only on top-level destinations, so the editors and the account
 * detail are full-screen. Insets are left to the screens themselves (`KsScreen` uses a
 * top app bar), which is why the shell Scaffold declares empty window insets - one owner
 * per inset, never two.
 */
@Composable
fun KsWalletApp(
    modifier: Modifier = Modifier,
    observeSettings: ObserveSettingsUseCase = koinInject(),
) {
    // The settings are read *above* the theme: a change of theme mode or of the base currency
    // re-composes the whole app from here, which is the only place that has to know about it.
    val settings by observeSettings().collectAsStateWithLifecycle(initialValue = UserSettings())

    KsWalletTheme(
        darkTheme = when (settings.themeMode) {
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
        },
        dynamicColor = settings.dynamicColor,
    ) {
        val navController = rememberNavController()
        // Reading the back stack entry is what makes the bar follow navigation:
        // `isOnDestination` alone would not recompose when the destination changes.
        val backStackEntry by navController.currentBackStackEntryAsState()
        val selected = remember(backStackEntry) {
            KsWalletTopLevelDestination.entries.firstOrNull { navController.isOnDestination(it.route) }
        }

        Scaffold(
            modifier = modifier,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (selected != null) {
                    NavigationBar {
                        KsWalletTopLevelDestination.entries.forEach { destination ->
                            NavigationBarItem(
                                selected = destination == selected,
                                onClick = { navController.navigateTopLevelTo(destination) },
                                icon = {
                                    Icon(imageVector = destination.icon, contentDescription = null)
                                },
                                label = { Text(text = stringResource(destination.labelRes)) },
                            )
                        }
                    }
                }
            },
        ) { padding ->
            KsWalletNavHost(
                navController = navController,
                modifier = Modifier.padding(padding),
            )
        }
    }
}
