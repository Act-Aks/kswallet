package com.kswallet.app.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.kswallet.core.designsystem.component.KsWalletAnimatedEdgeContainer
import com.kswallet.core.designsystem.component.KsWalletBottomBar
import com.kswallet.core.designsystem.component.KsWalletBottomBarStyle
import com.kswallet.core.navigation.Navigator
import com.kswallet.core.navigation.rememberNavigationState
import com.kswallet.core.navigation.toEntries
import com.kswallet.feature.accounts.presentation.navigation.AccountDetailRoute
import com.kswallet.feature.accounts.presentation.navigation.AccountEditorRoute
import com.kswallet.feature.accounts.presentation.navigation.AccountsRoute
import com.kswallet.feature.accounts.presentation.navigation.accountsEntries
import com.kswallet.feature.budgets.presentation.navigation.BudgetEditorRoute
import com.kswallet.feature.budgets.presentation.navigation.budgetsEntries
import com.kswallet.feature.categories.presentation.navigation.CategoryEditorRoute
import com.kswallet.feature.categories.presentation.navigation.categoriesEntries
import com.kswallet.feature.home.presentation.navigation.HomeRoute
import com.kswallet.feature.home.presentation.navigation.homeEntries
import com.kswallet.feature.reports.presentation.navigation.reportsEntries
import com.kswallet.feature.settings.presentation.navigation.SettingsRoute
import com.kswallet.feature.settings.presentation.navigation.settingsEntries
import com.kswallet.feature.transactions.presentation.navigation.TransactionEditorRoute
import com.kswallet.feature.transactions.presentation.navigation.TransactionsRoute
import com.kswallet.feature.transactions.presentation.navigation.transactionsEntries

@Composable
fun KsWalletNavigationRoot(
    modifier: Modifier = Modifier,
) {
    val navigationState = rememberNavigationState(
        startRoute = HomeRoute,
        topLevelRoutes = topLevelRoutes,
        configuration = ksWalletSavedStateConfiguration,
    )

    val navigator = remember(navigationState) {
        Navigator(navigationState)
    }

    val currentRoute = navigationState.currentRoute
    val showBottomBar = currentRoute in topLevelRoutes

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            KsWalletAnimatedEdgeContainer(visible = showBottomBar) {
                KsWalletBottomBar(
                    tabs = ksWalletTabs,
                    selectedRoute = navigationState.topLevelRoute,
                    onSelectRoute = navigator::navigate,
                    style = KsWalletBottomBarStyle.EXPANDED,
                )
            }
        },
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            onBack = navigator::goBack,
            transitionSpec = {
                slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
            },
            popTransitionSpec = {
                slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
            },
            predictivePopTransitionSpec = {
                slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
            },
            entries = navigationState.toEntries(
                entryProvider {
                    homeEntries(
                        onNavigateToTransactions = { navigator.navigate(TransactionsRoute) },
                        onNavigateToAccounts = { navigator.navigate(AccountsRoute) },
                        onAddTransaction = { navigator.navigate(TransactionEditorRoute()) },
                        onAddAccount = { navigator.navigate(AccountEditorRoute()) },
                        onOpenTransaction = { id -> navigator.navigate(TransactionEditorRoute(id)) },
                        onOpenSettings = { navigator.navigate(SettingsRoute) },
                    )

                    transactionsEntries(
                        onNavigateBack = navigator::goBack,
                        onNavigateToEditor = { id -> navigator.navigate(TransactionEditorRoute(id)) },
                    )

                    reportsEntries(onNavigateBack = navigator::goBack)

                    budgetsEntries(
                        onNavigateBack = navigator::goBack,
                        onNavigateToEditor = { id -> navigator.navigate(BudgetEditorRoute(id)) },
                    )

                    settingsEntries(onNavigateBack = navigator::goBack)

                    accountsEntries(
                        onNavigateBack = navigator::goBack,
                        onNavigateToEditor = { id -> navigator.navigate(AccountEditorRoute(id)) },
                        onOpenAccount = { id -> navigator.navigate(AccountDetailRoute(id.value)) },
                        onOpenLedger = { navigator.navigate(TransactionsRoute) },
                    )

                    categoriesEntries(
                        onNavigateBack = navigator::goBack,
                        onNavigateToEditor = { id -> navigator.navigate(CategoryEditorRoute(id)) },
                    )
                },
            ),
        )
    }
}
