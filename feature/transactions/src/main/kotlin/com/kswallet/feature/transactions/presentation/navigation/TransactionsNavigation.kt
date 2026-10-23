package com.kswallet.feature.transactions.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kswallet.core.navigation.KsWalletDestination
import com.kswallet.feature.transactions.presentation.editor.TransactionEditorRoute
import com.kswallet.feature.transactions.presentation.list.TransactionsRoute
import kotlinx.serialization.Serializable

/** Destination owned by the transactions feature. */
@Serializable
data object TransactionsDestination : KsWalletDestination

/** Serializable contract for the "open the transaction editor" hand-off between features. */
@Serializable
data class TransactionEditorDestination(val transactionId: Long? = null) : KsWalletDestination

/**
 * The feature's public navigation surface.
 *
 * The editor route itself comes from `:core:navigation` ([TransactionEditorDestination])
 * because other features (home, accounts) also open it - a single shared contract instead of
 * one destination type per module.
 */
fun EntryProviderScope<NavKey>.transactionsEntries(
    onNavigateBack: () -> Unit,
    onNavigateToEditor: (transactionId: Long?) -> Unit,
) {
    entry<TransactionsDestination> {
        TransactionsRoute(onOpenEditor = { id -> onNavigateToEditor(id?.value) })
    }

    entry<TransactionEditorDestination> { destination ->
        TransactionEditorRoute(
            transactionIdValue = destination.transactionId ?: 0L,
            onNavigateBack = onNavigateBack,
        )
    }
}
