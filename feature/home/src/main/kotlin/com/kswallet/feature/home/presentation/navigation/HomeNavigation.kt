package com.kswallet.feature.home.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kswallet.core.navigation.KsWalletDestination
import com.kswallet.feature.home.presentation.home.HomeRoute
import kotlinx.serialization.Serializable

@Serializable
data object HomeDestination : KsWalletDestination

/**
 * The dashboard. It needs four hand-offs and owns none of the screens behind them: the app
 * wires them to the transactions/accounts graphs.
 */
fun NavGraphBuilder.homeScreen(
    onNavigateToTransactions: () -> Unit,
    onNavigateToAccounts: () -> Unit,
    onAddTransaction: () -> Unit,
    onAddAccount: () -> Unit,
    onOpenTransaction: (Long) -> Unit,
    onOpenSettings: () -> Unit,
) {
    composable<HomeDestination> {
        HomeRoute(
            onAddTransaction = onAddTransaction,
            onOpenTransaction = onOpenTransaction,
            onAddAccount = onAddAccount,
            onNavigateToAccounts = onNavigateToAccounts,
            onNavigateToTransactions = onNavigateToTransactions,
            onOpenSettings = onOpenSettings,
        )
    }
}
