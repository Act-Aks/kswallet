package com.kswallet.feature.transactions.domain.validation

sealed interface TransactionFormError {
    data object InvalidAmount : TransactionFormError
    data object AmountNotPositive : TransactionFormError
    data object AccountMissing : TransactionFormError
    data object TransferTargetMissing : TransactionFormError
    data object TransferTargetSameAsSource : TransactionFormError
    data object CategoryMissing : TransactionFormError
    data object CurrencyMismatch : TransactionFormError
    data object TransferNotAllowed : TransactionFormError
}
