package com.kswallet.feature.transactions.presentation.error

import com.kswallet.core.common.result.DomainError
import com.kswallet.core.mvi.UiMessage
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.feature.transactions.R
import com.kswallet.feature.transactions.presentation.editor.TransactionEditorError

fun DomainError.toUiMessage(): UiMessage = when (this) {
    DomainError.NotFound -> UiMessage.Resource(R.string.error_not_found)
    is DomainError.Conflict -> UiMessage.Plain(reason)
    is DomainError.Data -> UiMessage.Resource(R.string.error_storage)
    is DomainError.Unknown -> UiMessage.Resource(R.string.error_unknown)
}

fun TransactionEditorError.toUiMessage(): UiMessage = when (this) {
    TransactionEditorError.InvalidAmount -> UiMessage.Resource(R.string.transaction_error_invalid_amount)
    TransactionEditorError.AmountNotPositive -> UiMessage.Resource(R.string.transaction_error_amount_not_positive)
    TransactionEditorError.AccountMissing -> UiMessage.Resource(R.string.transaction_error_account_missing)
    TransactionEditorError.TransferTargetMissing -> UiMessage.Resource(R.string.transaction_error_transfer_missing)
    TransactionEditorError.TransferTargetSameAsSource -> UiMessage.Resource(R.string.transaction_error_transfer_same)
    TransactionEditorError.CategoryMissing -> UiMessage.Resource(R.string.transaction_error_category_missing)
    TransactionEditorError.CurrencyMismatch -> UiMessage.Resource(R.string.transaction_error_currency_mismatch)
    TransactionEditorError.Unknown -> UiMessage.Resource(R.string.error_unknown)
}
