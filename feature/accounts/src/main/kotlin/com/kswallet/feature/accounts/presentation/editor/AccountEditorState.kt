package com.kswallet.feature.accounts.presentation.editor

import com.kswallet.core.model.AccountType
import com.kswallet.core.model.Currency
import com.kswallet.core.ui.screen.ScreenState

data class AccountEditorState(
    val isLoading: Boolean = true,
    val isNew: Boolean = true,
    val name: String = "",
    val type: AccountType = AccountType.CASH,
    val currency: Currency = Currency.Default,
    val initialBalanceText: String = "",
    val colorArgb: Long = 0,
    val includedInTotal: Boolean = true,
    val nameError: AccountEditorError? = null,
    val balanceError: AccountEditorError? = null,
    val isSaving: Boolean = false,
) : ScreenState {
    /** The currency a brand-new account should default to, before settings are read. */
    val canEditBalance: Boolean get() = isNew || true
}
