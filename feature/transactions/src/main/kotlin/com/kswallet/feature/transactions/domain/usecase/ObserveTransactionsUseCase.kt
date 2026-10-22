package com.kswallet.feature.transactions.domain.usecase

import com.kswallet.feature.transactions.domain.model.TransactionDetails
import com.kswallet.feature.transactions.domain.model.TransactionFilter
import com.kswallet.feature.transactions.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class ObserveTransactionsUseCase(
    private val repository: TransactionRepository,
    private val resolvePeriod: ResolvePeriodUseCase,
) {
    operator fun invoke(filter: TransactionFilter): Flow<List<TransactionDetails>> = repository.observeTransactions(
        range = resolvePeriod(filter.period),
        type = filter.type,
        accountId = filter.accountId,
        categoryId = filter.categoryId,
    )
}
