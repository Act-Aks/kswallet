package com.kswallet.feature.accounts.presentation.editor

import com.kswallet.core.model.AccountType
import com.kswallet.core.model.Currency
import com.kswallet.core.ui.screen.ScreenAction

sealed interface AccountEditorAction : ScreenAction {
    data class NameChanged(val name: String) : AccountEditorAction
    data class TypeChanged(val type: AccountType) : AccountEditorAction
    data class CurrencyChanged(val currency: Currency) : AccountEditorAction
    data class InitialBalanceChanged(val text: String) : AccountEditorAction
    data class ColorChanged(val colorArgb: Long) : AccountEditorAction
    data class IncludedInTotalChanged(val included: Boolean) : AccountEditorAction
    data object SaveClicked : AccountEditorAction
    data object BackClicked : AccountEditorAction
}
