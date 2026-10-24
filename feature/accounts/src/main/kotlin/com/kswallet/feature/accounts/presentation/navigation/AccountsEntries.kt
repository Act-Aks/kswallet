package com.kswallet.feature.accounts.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kswallet.core.model.AccountId
import com.kswallet.feature.accounts.presentation.detail.AccountDetailRoot
import com.kswallet.feature.accounts.presentation.editor.AccountEditorRoot
import com.kswallet.feature.accounts.presentation.list.AccountsRoot

/**
 * Exposed graph of the accounts feature. `onOpenAccount` is how the app routes an account
 * tap to the transactions feature - the accounts module never imports it.
 */
fun EntryProviderScope<NavKey>.accountsEntries(
    onNavigateBack: () -> Unit,
    onNavigateToEditor: (accountId: Long?) -> Unit,
    onOpenAccount: (AccountId) -> Unit,
    onOpenLedger: () -> Unit,
) {
    entry<AccountsRoute> {
        AccountsRoot(
            onAddAccount = { onNavigateToEditor(null) },
            onOpenAccount = onOpenAccount,
        )
    }

    entry<AccountEditorRoute> { destination ->
        AccountEditorRoot(
            accountIdValue = destination.accountId ?: 0L,
            onNavigateBack = onNavigateBack,
        )
    }

    entry<AccountDetailRoute> { destination ->
        AccountDetailRoot(
            accountIdValue = destination.accountId,
            onNavigateBack = onNavigateBack,
            onEdit = { onNavigateToEditor(destination.accountId) },
            onOpenLedger = onOpenLedger,
        )
    }
}
