package com.kswallet.feature.transactions.domain.usecase

import com.kswallet.core.common.result.Outcome
import com.kswallet.core.model.TransactionId
import com.kswallet.feature.transactions.domain.repository.TransactionRepository

class DeleteTransactionUseCase(
    private val repository: TransactionRepository,
) {
    suspend operator fun invoke(transactionId: TransactionId): Outcome<Unit> =
        repository.deleteTransaction(transactionId)
}
