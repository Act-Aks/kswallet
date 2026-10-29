package com.kswallet.feature.accounts.presentation

import com.kswallet.core.screen.ScreenAction

sealed interface AccountsAction : ScreenAction {

    data object RetryClicked : AccountsAction

    data object ShowCreateAccountClicked : AccountsAction

    data object DismissCreateAccountClicked : AccountsAction

    data class NameChanged(
        val value: String,
    ) : AccountsAction

    data class CurrencySelected(
        val code: String,
    ) : AccountsAction

    data object SubmitCreateAccountClicked : AccountsAction
}