package com.kswallet.feature.accounts.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kswallet.core.model.AccountId
import com.kswallet.core.navigation.KsWalletDestination
import com.kswallet.feature.accounts.presentation.detail.AccountDetailRoute
import com.kswallet.feature.accounts.presentation.editor.AccountEditorRoute
import com.kswallet.feature.accounts.presentation.list.AccountsRoute
import kotlinx.serialization.Serializable

@Serializable
data object AccountsDestination : KsWalletDestination

@Serializable
data class AccountEditorDestination(val accountId: Long? = null) : KsWalletDestination

/** Serializable contract for the "open the account detail" hand-off between features. */
@Serializable
data class AccountDetailDestination(val accountId: Long) : KsWalletDestination

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
    entry<AccountsDestination> {
        AccountsRoute(
            onAddAccount = { onNavigateToEditor(null) },
            onOpenAccount = onOpenAccount,
        )
    }

    entry<AccountEditorDestination> { destination ->
        AccountEditorRoute(
            accountIdValue = destination.accountId ?: 0L,
            onNavigateBack = onNavigateBack,
        )
    }

    entry<AccountDetailDestination> { destination ->
        AccountDetailRoute(
            accountIdValue = destination.accountId,
            onNavigateBack = onNavigateBack,
            onEdit = { onNavigateToEditor(destination.accountId) },
            onOpenLedger = onOpenLedger,
        )
    }
}
