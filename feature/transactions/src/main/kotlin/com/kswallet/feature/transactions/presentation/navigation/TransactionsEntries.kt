package com.kswallet.feature.transactions.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kswallet.feature.transactions.presentation.editor.TransactionEditorRoot
import com.kswallet.feature.transactions.presentation.list.TransactionsRoot

/**
 * The feature's public navigation surface.
 *
 * The editor route itself comes from `:core:navigation` ([TransactionEditorRoot])
 * because other features (home, accounts) also open it - a single shared contract instead of
 * one destination type per module.
 */
fun EntryProviderScope<NavKey>.transactionsEntries(
    onNavigateBack: () -> Unit,
    onNavigateToEditor: (transactionId: Long?) -> Unit,
) {
    entry<TransactionsRoute> {
        TransactionsRoot(onOpenEditor = { id -> onNavigateToEditor(id?.value) })
    }

    entry<TransactionEditorRoute> { destination ->
        TransactionEditorRoot(
            transactionIdValue = destination.transactionId ?: 0L,
            onNavigateBack = onNavigateBack,
        )
    }
}
