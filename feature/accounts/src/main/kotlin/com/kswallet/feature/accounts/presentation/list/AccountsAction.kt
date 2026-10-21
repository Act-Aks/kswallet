package com.kswallet.feature.accounts.presentation.list

import com.kswallet.core.model.AccountId
import com.kswallet.core.ui.screen.ScreenAction

sealed interface AccountsAction : ScreenAction {
    data object AddAccountClicked : AccountsAction
    data class AccountClicked(val id: AccountId) : AccountsAction
    data class DeleteClicked(val id: AccountId) : AccountsAction
    data object DeleteConfirmed : AccountsAction
    data object DeleteDismissed : AccountsAction
}
