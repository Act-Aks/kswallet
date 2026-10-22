package com.kswallet.feature.transactions.presentation.editor

import com.kswallet.core.model.AccountId
import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.CategoryType
import com.kswallet.core.model.Currency
import com.kswallet.core.model.TransactionType
import com.kswallet.core.ui.screen.ScreenState

data class TransactionEditorState(
    val isLoading: Boolean = true,
    val isNew: Boolean = true,
    val type: TransactionType = TransactionType.EXPENSE,
    val amountText: String = "",
    val note: String = "",
    val accountId: AccountId? = null,
    val transferAccountId: AccountId? = null,
    val categoryId: CategoryId? = null,
    val accounts: List<AccountOptionUi> = emptyList(),
    val categories: List<CategoryOptionUi> = emptyList(),
    val dateLabel: String = "",
    val errors: Set<TransactionEditorError> = emptySet(),
    val isSaving: Boolean = false,
) : ScreenState {

    val selectedAccount: AccountOptionUi? get() = accounts.firstOrNull { it.id == accountId }

    val currency: Currency get() = selectedAccount?.currency ?: Currency.Default

    /** Only categories matching the entry type make sense, so the dropdown filters itself. */
    val selectableCategories: List<CategoryOptionUi>
        get() = categories.filter { it.type == CategoryType.forTransactionType(type) }

    val needsCategory: Boolean get() = type != TransactionType.TRANSFER

    val needsTransferTarget: Boolean get() = type == TransactionType.TRANSFER

    val canSave: Boolean get() = !isSaving
}

