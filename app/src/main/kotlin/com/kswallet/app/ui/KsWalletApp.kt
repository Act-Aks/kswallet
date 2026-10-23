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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.kswallet.app.navigation.KsNavHost
import com.kswallet.app.navigation.KsWalletTopLevelDestination
import com.kswallet.core.datastore.domain.model.ThemeMode
import com.kswallet.core.datastore.domain.model.UserSettings
import com.kswallet.core.designsystem.theme.KsWalletTheme
import com.kswallet.core.navigation.isOnDestination
import com.kswallet.feature.accounts.presentation.navigation.AccountsDestination
import com.kswallet.feature.budgets.presentation.navigation.BudgetsDestination
import com.kswallet.feature.categories.presentation.navigation.CategoriesDestination
import com.kswallet.feature.home.presentation.navigation.HomeDestination
import com.kswallet.feature.reports.presentation.navigation.ReportsDestination
import com.kswallet.feature.settings.domain.usecase.ObserveSettingsUseCase
import com.kswallet.feature.transactions.presentation.navigation.TransactionsDestination
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
    modifier: Modifier = Modifier,
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
        val homeBackStack = rememberNavBackStack(HomeDestination)
        val transactionsBackStack = rememberNavBackStack(TransactionsDestination)
        val reportsBackStack = rememberNavBackStack(ReportsDestination)
        val budgetsBackStack = rememberNavBackStack(BudgetsDestination)
        val accountsBackStack = rememberNavBackStack(AccountsDestination)
        val categoriesBackStack = rememberNavBackStack(CategoriesDestination)

        var selectedTabName by rememberSaveable { mutableStateOf(KsWalletTopLevelDestination.HOME.name) }
        val selectedTab = KsWalletTopLevelDestination.valueOf(selectedTabName)
        val backStack: NavBackStack<NavKey> = when (selectedTab) {
            KsWalletTopLevelDestination.HOME -> homeBackStack
            KsWalletTopLevelDestination.TRANSACTIONS -> transactionsBackStack
            KsWalletTopLevelDestination.REPORTS -> reportsBackStack
            KsWalletTopLevelDestination.BUDGETS -> budgetsBackStack
            KsWalletTopLevelDestination.ACCOUNTS -> accountsBackStack
            KsWalletTopLevelDestination.CATEGORIES -> categoriesBackStack
        }
        val selectedDestination = KsWalletTopLevelDestination.entries.firstOrNull {
            backStack.isOnDestination(it.route)
        }

        Scaffold(
            modifier = modifier,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (selectedDestination != null) {
                    NavigationBar {
                        KsWalletTopLevelDestination.entries.forEach { destination ->
                            NavigationBarItem(
                                selected = destination == selectedDestination,
                                onClick = { selectedTabName = destination.name },
                                icon = { Icon(imageVector = destination.icon, contentDescription = null) },
                                label = { Text(text = stringResource(destination.labelRes)) },
                            )
                        }
                    }
                }
            },
        ) { padding ->
            KsNavHost(
                backStack = backStack,
                onNavigateTopLevel = { destination -> selectedTabName = destination.name },
                onBackAtRoot = {
                    if (selectedTab != KsWalletTopLevelDestination.HOME) {
                        selectedTabName = KsWalletTopLevelDestination.HOME.name
                    }
                },
                modifier = Modifier.padding(padding),
            )
        }
    }
}
