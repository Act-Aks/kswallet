package com.kswallet.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.kswallet.core.navigation.navigateTo
import com.kswallet.core.navigation.popBackStack
import com.kswallet.feature.accounts.presentation.navigation.AccountDetailDestination
import com.kswallet.feature.accounts.presentation.navigation.AccountEditorDestination
import com.kswallet.feature.accounts.presentation.navigation.accountsEntries
import com.kswallet.feature.budgets.presentation.navigation.BudgetEditorDestination
import com.kswallet.feature.budgets.presentation.navigation.budgetsEntries
import com.kswallet.feature.categories.presentation.navigation.CategoryEditorDestination
import com.kswallet.feature.categories.presentation.navigation.categoriesEntries
import com.kswallet.feature.home.presentation.navigation.homeEntries
import com.kswallet.feature.reports.presentation.navigation.reportsEntries
import com.kswallet.feature.settings.presentation.navigation.SettingsDestination
import com.kswallet.feature.settings.presentation.navigation.settingsEntries
import com.kswallet.feature.transactions.presentation.navigation.TransactionEditorDestination
import com.kswallet.feature.transactions.presentation.navigation.transactionsEntries

/**
 * The app's Navigation 3 display. Features contribute entry builders; this composition root
 * wires cross-feature hand-offs and owns the active back stack.
 */
@Composable
fun KsNavHost(
    backStack: NavBackStack<NavKey>,
    onNavigateTopLevel: (KsWalletTopLevelDestination) -> Unit,
    onBackAtRoot: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavDisplay(
        backStack = backStack,
        onBack = {
            if (!backStack.popBackStack()) onBackAtRoot()
        },
        modifier = modifier,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider<NavKey> {
            homeEntries(
                onNavigateToTransactions = { onNavigateTopLevel(KsWalletTopLevelDestination.TRANSACTIONS) },
                onNavigateToAccounts = { onNavigateTopLevel(KsWalletTopLevelDestination.ACCOUNTS) },
                onAddTransaction = { backStack.navigateTo(TransactionEditorDestination()) },
                onAddAccount = { backStack.navigateTo(AccountEditorDestination()) },
                onOpenTransaction = { id -> backStack.navigateTo(TransactionEditorDestination(id)) },
                onOpenSettings = { backStack.navigateTo(SettingsDestination) },
            )

            transactionsEntries(
                onNavigateBack = { backStack.popBackStack() },
                onNavigateToEditor = { id -> backStack.navigateTo(TransactionEditorDestination(id)) },
            )

            reportsEntries(
                onNavigateBack = { backStack.popBackStack() },
            )

            budgetsEntries(
                onNavigateBack = { backStack.popBackStack() },
                onNavigateToEditor = { id -> backStack.navigateTo(BudgetEditorDestination(id)) },
            )

            settingsEntries(
                onNavigateBack = { backStack.popBackStack() },
            )

            accountsEntries(
                onNavigateBack = { backStack.popBackStack() },
                onNavigateToEditor = { id -> backStack.navigateTo(AccountEditorDestination(id)) },
                onOpenAccount = { id -> backStack.navigateTo(AccountDetailDestination(id.value)) },
                onOpenLedger = { onNavigateTopLevel(KsWalletTopLevelDestination.TRANSACTIONS) },
            )

            categoriesEntries(
                onNavigateBack = { backStack.popBackStack() },
                onNavigateToEditor = { id -> backStack.navigateTo(CategoryEditorDestination(id)) },
            )
        },
    )
}
