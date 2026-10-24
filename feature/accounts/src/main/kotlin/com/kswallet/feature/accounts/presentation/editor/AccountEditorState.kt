package com.kswallet.feature.accounts.presentation.editor

import com.kswallet.core.model.AccountType
import com.kswallet.core.model.Currency
import com.kswallet.core.ui.screen.ScreenState

data class AccountEditorState(
    val balanceError: AccountEditorError? = null,
    val colorArgb: Long = 0,
    val currency: Currency = Currency.Default,
    val includedInTotal: Boolean = true,
    val initialBalanceText: String = "",
    val isLoading: Boolean = true,
    val isNew: Boolean = true,
    val isSaving: Boolean = false,
    val name: String = "",
    val nameError: AccountEditorError? = null,
    val type: AccountType = AccountType.CASH,
) : ScreenState {
    /** The currency a brand-new account should default to, before settings are read. */
    val canEditBalance: Boolean get() = isNew || true
}
