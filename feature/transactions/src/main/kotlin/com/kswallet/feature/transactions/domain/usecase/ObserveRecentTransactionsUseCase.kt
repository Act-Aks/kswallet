package com.kswallet.feature.transactions.domain.usecase

import com.kswallet.feature.transactions.domain.model.TransactionDetails
import com.kswallet.feature.transactions.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

/** The dashboard asks for "the latest N" without caring about periods. */
class ObserveRecentTransactionsUseCase(
    private val repository: TransactionRepository,
) {
    operator fun invoke(limit: Int = 5): Flow<List<TransactionDetails>> = repository.observeRecentTransactions(limit)
}
