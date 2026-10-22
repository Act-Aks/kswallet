package com.kswallet.feature.transactions.presentation.editor

import com.kswallet.core.model.AccountId
import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.TransactionType
import com.kswallet.core.ui.screen.ScreenAction

sealed interface TransactionEditorAction : ScreenAction {
    data class TypeChanged(val type: TransactionType) : TransactionEditorAction
    data class AmountChanged(val text: String) : TransactionEditorAction
    data class AccountSelected(val id: AccountId) : TransactionEditorAction
    data class TransferAccountSelected(val id: AccountId) : TransactionEditorAction
    data class CategorySelected(val id: CategoryId) : TransactionEditorAction
    data class NoteChanged(val note: String) : TransactionEditorAction
    data object SaveClicked : TransactionEditorAction
    data object BackClicked : TransactionEditorAction
}
