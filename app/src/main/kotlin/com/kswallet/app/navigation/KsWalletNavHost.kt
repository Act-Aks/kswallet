package com.kswallet.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.kswallet.app.ui.AccountDetailRoute
import com.kswallet.core.navigation.AccountDetailDestination
import com.kswallet.core.navigation.TransactionEditorDestination
import com.kswallet.core.navigation.navigateTo
import com.kswallet.feature.accounts.presentation.navigation.AccountEditorDestination
import com.kswallet.feature.accounts.presentation.navigation.accountsScreen
import com.kswallet.feature.budgets.presentation.navigation.BudgetEditorDestination
import com.kswallet.feature.budgets.presentation.navigation.budgetsScreen
import com.kswallet.feature.categories.presentation.navigation.CategoryEditorDestination
import com.kswallet.feature.categories.presentation.navigation.categoriesScreen
import com.kswallet.feature.home.presentation.navigation.homeScreen
import com.kswallet.feature.reports.presentation.navigation.reportsScreen
import com.kswallet.feature.settings.presentation.navigation.SettingsDestination
import com.kswallet.feature.settings.presentation.navigation.settingsScreen
import com.kswallet.feature.transactions.presentation.navigation.transactionsScreen

/**
 * The app's navigation graph.
 *
 * This is the one file that knows every feature: each feature contributes its own
 * `NavGraphBuilder.<feature>Screen(...)` extension, and the hand-offs between them
 * (home -> editor, accounts -> account detail) are wired here. No feature imports another
 * feature's screens.
 */
@Composable
fun KsWalletNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = KsWalletTopLevelDestination.HOME.route,
        modifier = modifier,
    ) {
        homeScreen(
            onNavigateToTransactions = { navController.navigateTopLevelTo(KsWalletTopLevelDestination.TRANSACTIONS) },
            onNavigateToAccounts = { navController.navigateTopLevelTo(KsWalletTopLevelDestination.ACCOUNTS) },
            onAddTransaction = { navController.navigateTo(TransactionEditorDestination()) },
            onAddAccount = { navController.navigateTo(AccountEditorDestination()) },
            onOpenTransaction = { id -> navController.navigateTo(TransactionEditorDestination(id)) },
            onOpenSettings = { navController.navigateTo(SettingsDestination) },
        )

        transactionsScreen(
            onNavigateBack = navController::popBackStack,
            onNavigateToEditor = { id -> navController.navigateTo(TransactionEditorDestination(id)) },
        )

        reportsScreen(
            onNavigateBack = navController::popBackStack,
        )

        budgetsScreen(
            onNavigateBack = navController::popBackStack,
            onNavigateToEditor = { id -> navController.navigateTo(BudgetEditorDestination(id)) },
        )

        settingsScreen(
            onNavigateBack = navController::popBackStack,
        )

        accountsScreen(
            onNavigateBack = navController::popBackStack,
            onNavigateToEditor = { id -> navController.navigateTo(AccountEditorDestination(id)) },
            onOpenAccount = { id -> navController.navigateTo(AccountDetailDestination(id.value)) },
        )

        categoriesScreen(
            onNavigateBack = navController::popBackStack,
            onNavigateToEditor = { id -> navController.navigateTo(CategoryEditorDestination(id)) },
        )

        // The account detail is the only screen the app owns itself for now: it is a thin
        // read-only view over the accounts domain and will move into :feature:accounts once
        // it grows transaction history of its own.
        composable<AccountDetailDestination> { backStackEntry ->
            val destination = backStackEntry.toRoute<AccountDetailDestination>()
            AccountDetailRoute(
                accountIdValue = destination.accountId,
                onNavigateBack = navController::popBackStack,
                onEdit = { navController.navigateTo(AccountEditorDestination(destination.accountId)) },
                onOpenLedger = { navController.navigateTopLevelTo(KsWalletTopLevelDestination.TRANSACTIONS) },
            )
        }
    }
}

