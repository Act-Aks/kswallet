package com.kswallet.feature.transactions.presentation.editor

enum class TransactionEditorError {
    InvalidAmount,
    AmountNotPositive,
    AccountMissing,
    TransferTargetMissing,
    TransferTargetSameAsSource,
    CategoryMissing,
    CurrencyMismatch,
    Unknown,
}
