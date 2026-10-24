package com.kswallet.app.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kswallet.core.navigation.Navigator
import com.kswallet.feature.accounts.presentation.navigation.AccountDetailRoute
import com.kswallet.feature.accounts.presentation.navigation.AccountEditorRoute
import com.kswallet.feature.accounts.presentation.navigation.AccountsRoute
import com.kswallet.feature.accounts.presentation.navigation.accountsEntries
import com.kswallet.feature.budgets.presentation.navigation.BudgetEditorRoute
import com.kswallet.feature.budgets.presentation.navigation.budgetsEntries
import com.kswallet.feature.categories.presentation.navigation.CategoryEditorRoute
import com.kswallet.feature.categories.presentation.navigation.categoriesEntries
import com.kswallet.feature.home.presentation.navigation.homeEntries
import com.kswallet.feature.reports.presentation.navigation.reportsEntries
import com.kswallet.feature.settings.presentation.navigation.SettingsRoute
import com.kswallet.feature.settings.presentation.navigation.settingsEntries
import com.kswallet.feature.transactions.presentation.navigation.TransactionEditorRoute
import com.kswallet.feature.transactions.presentation.navigation.TransactionsRoute
import com.kswallet.feature.transactions.presentation.navigation.transactionsEntries

/**
 * Registers wallet destinations and connects feature navigation callbacks
 * to the app's [Navigator].
 *
 * Cross-feature route knowledge remains in the app module rather than
 * leaking into individual feature implementations.
 */
internal fun EntryProviderScope<NavKey>.ksWalletEntries(navigator: Navigator) {
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
}
