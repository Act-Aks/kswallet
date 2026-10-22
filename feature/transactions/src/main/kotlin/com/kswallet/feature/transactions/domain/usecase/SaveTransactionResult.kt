package com.kswallet.feature.transactions.domain.usecase

import com.kswallet.core.common.result.DomainError
import com.kswallet.core.model.TransactionId
import com.kswallet.feature.transactions.domain.validation.TransactionFormError

sealed interface SaveTransactionResult {
    data class Saved(val id: TransactionId) : SaveTransactionResult
    data class Invalid(val errors: List<TransactionFormError>) : SaveTransactionResult
    data class Failed(val error: DomainError) : SaveTransactionResult
}
