package com.kswallet.feature.accounts.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kswallet.feature.accounts.presentation.accounts.AccountsRoot
import com.kswallet.feature.accounts.presentation.create_account.CreateAccountRoot
import com.kswallet.feature.accounts.presentation.edit_account.EditAccountRoot

/**
 * Exposed graph of the accounts feature. `onOpenAccount` is how the app routes an account
 * tap to the transactions feature - the accounts module never imports it.
 */
fun EntryProviderScope<NavKey>.accountsEntries(
    onAccountUpdate: () -> Unit,
    onAccountCreate: () -> Unit,
    onNavigateBack: () -> Unit,
    onOpenAccount: (String) -> Unit,
) {
    entry<AccountsRoute> {
        AccountsRoot(
            onCreateAccountClick = onAccountCreate,
            onAccountClick = onOpenAccount
        )
    }

    entry<CreateAccountRoute> {
        CreateAccountRoot(
            onAccountCreated = onNavigateBack
        )
    }

    entry<EditAccountRoute> { route ->
        EditAccountRoot(
            accountId = route.accountId,
            onAccountUpdated = onAccountUpdate,
        )
    }
}