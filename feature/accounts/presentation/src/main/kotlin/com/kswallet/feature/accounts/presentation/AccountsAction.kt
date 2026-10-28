package com.kswallet.feature.accounts.presentation

import com.kswallet.core.screen.ScreenAction

sealed interface AccountsAction : ScreenAction {

    data object RetryClicked : AccountsAction
}