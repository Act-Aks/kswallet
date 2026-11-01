package com.kswallet.feature.accounts.presentation.accounts

import com.kswallet.core.screen.ScreenAction

sealed interface AccountsAction : ScreenAction {
    data object RetryClicked : AccountsAction
    data object CreateAccountClicked : AccountsAction
    data class AccountClicked(val accountId: String) : AccountsAction
}