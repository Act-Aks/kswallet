package com.kswallet.feature.home.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kswallet.feature.home.presentation.home.HomeRoot


/**
 * The dashboard. It needs four hand-offs and owns none of the screens behind them: the app
 * wires them to the transactions/accounts graphs.
 */
fun EntryProviderScope<NavKey>.homeEntries(
    onNavigateToTransactions: () -> Unit,
    onNavigateToAccounts: () -> Unit,
    onAddTransaction: () -> Unit,
    onAddAccount: () -> Unit,
    onOpenTransaction: (Long) -> Unit,
    onOpenSettings: () -> Unit,
) {
    entry<HomeRoute> {
        HomeRoot(
            onAddTransaction = onAddTransaction,
            onOpenTransaction = onOpenTransaction,
            onAddAccount = onAddAccount,
            onNavigateToAccounts = onNavigateToAccounts,
            onNavigateToTransactions = onNavigateToTransactions,
            onOpenSettings = onOpenSettings,
        )
    }
}
