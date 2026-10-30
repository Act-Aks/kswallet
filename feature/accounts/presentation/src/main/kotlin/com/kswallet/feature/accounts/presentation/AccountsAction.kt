package com.kswallet.feature.accounts.presentation

import com.kswallet.core.screen.ScreenAction

sealed interface AccountsAction : ScreenAction {

    data class ArchiveToggleClicked(val accountId: String) : AccountsAction
    data class CurrencySelected(val code: String) : AccountsAction
    data class NameChanged(val value: String) : AccountsAction
    data class RenameClicked(val accountId: String) : AccountsAction
    
    data object DismissCreateAccountClicked : AccountsAction
    data object RenameCancelled : AccountsAction
    data object RenameSubmitted : AccountsAction
    data object RetryClicked : AccountsAction
    data object ShowCreateAccountClicked : AccountsAction
    data object SubmitCreateAccountClicked : AccountsAction
}