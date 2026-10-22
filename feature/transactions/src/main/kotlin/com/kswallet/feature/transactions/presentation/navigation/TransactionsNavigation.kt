package com.kswallet.feature.transactions.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.kswallet.core.navigation.KsWalletDestination
import com.kswallet.core.navigation.TransactionEditorDestination
import com.kswallet.feature.transactions.presentation.editor.TransactionEditorRoute
import com.kswallet.feature.transactions.presentation.list.TransactionsRoute
import kotlinx.serialization.Serializable

/** Destination owned by the transactions feature. */
@Serializable
data object TransactionsDestination : KsWalletDestination

/**
 * The feature's public navigation surface.
 *
 * The editor route itself comes from `:core:navigation` ([TransactionEditorDestination])
 * because other features (home, accounts) also open it - a single shared contract instead of
 * one destination type per module.
 */
fun NavGraphBuilder.transactionsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEditor: (transactionId: Long?) -> Unit,
) {
    composable<TransactionsDestination> {
        TransactionsRoute(onOpenEditor = { id -> onNavigateToEditor(id?.value) })
    }

    composable<TransactionEditorDestination> { backStackEntry ->
        val destination = backStackEntry.toRoute<TransactionEditorDestination>()
        TransactionEditorRoute(
            transactionIdValue = destination.transactionId ?: 0L,
            onNavigateBack = onNavigateBack,
        )
    }
}
