package com.kswallet.feature.accounts.presentation.create_account

import com.kswallet.core.screen.ScreenAction

sealed interface CreateAccountAction : ScreenAction {
    data class NameChanged(val value: String) : CreateAccountAction
    data class CurrencySelected(val code: String) : CreateAccountAction
    data object SubmitClicked : CreateAccountAction
}