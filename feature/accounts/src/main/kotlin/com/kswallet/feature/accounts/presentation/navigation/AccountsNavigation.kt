package com.kswallet.feature.accounts.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.kswallet.core.model.AccountId
import com.kswallet.core.navigation.KsWalletDestination
import com.kswallet.feature.accounts.presentation.editor.AccountEditorRoute
import com.kswallet.feature.accounts.presentation.list.AccountsRoute
import kotlinx.serialization.Serializable

@Serializable
data object AccountsDestination : KsWalletDestination

@Serializable
data class AccountEditorDestination(val accountId: Long? = null) : KsWalletDestination

/**
 * Exposed graph of the accounts feature. `onOpenAccount` is how the app routes an account
 * tap to the transactions feature - the accounts module never imports it.
 */
fun NavGraphBuilder.accountsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEditor: (accountId: Long?) -> Unit,
    onOpenAccount: (AccountId) -> Unit,
) {
    composable<AccountsDestination> {
        AccountsRoute(
            onAddAccount = { onNavigateToEditor(null) },
            onOpenAccount = onOpenAccount,
        )
    }

    composable<AccountEditorDestination> { backStackEntry ->
        val destination = backStackEntry.toRoute<AccountEditorDestination>()
        AccountEditorRoute(
            accountIdValue = destination.accountId ?: 0L,
            onNavigateBack = onNavigateBack,
        )
    }
}
