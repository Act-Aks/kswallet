package com.kswallet.feature.transactions.domain.usecase

import com.kswallet.core.model.Currency
import com.kswallet.feature.transactions.domain.model.CashFlow
import com.kswallet.feature.transactions.domain.model.PeriodOption
import com.kswallet.feature.transactions.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class ObserveCashFlowUseCase(
    private val repository: TransactionRepository,
    private val resolvePeriod: ResolvePeriodUseCase,
) {
    operator fun invoke(period: PeriodOption, currency: Currency): Flow<CashFlow> =
        repository.observeCashFlow(resolvePeriod(period), currency)
}
