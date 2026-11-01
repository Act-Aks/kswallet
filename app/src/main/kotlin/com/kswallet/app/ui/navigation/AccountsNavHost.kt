package com.kswallet.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.kswallet.feature.accounts.presentation.accounts.AccountsRoot
import com.kswallet.feature.accounts.presentation.create_account.CreateAccountRoot
import com.kswallet.feature.accounts.presentation.edit_account.EditAccountRoot

@Composable
fun AccountsNavHost(
    modifier: Modifier = Modifier,
) {
    val backStack = rememberNavBackStack(AccountsRoute)

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = {
            backStack.removeLastOrNull()
        },
        entryProvider = entryProvider {
            entry<AccountsRoute> {
                AccountsRoot(
                    onCreateAccountClick = {
                        backStack.add(CreateAccountRoute)
                    },
                    onAccountClick = { accountId ->
                        backStack.add(
                            EditAccountRoute(accountId = accountId),
                        )
                    },
                )
            }

            entry<CreateAccountRoute> {
                CreateAccountRoot(
                    onAccountCreated = {
                        backStack.removeLastOrNull()
                    },
                )
            }

            entry<EditAccountRoute> { destination ->
                EditAccountRoot(
                    accountId = destination.accountId,
                    onAccountUpdated = {
                        backStack.removeLastOrNull()
                    },
                )
            }
        },
    )
}