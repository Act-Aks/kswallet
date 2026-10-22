package com.kswallet.feature.home.domain.usecase

import com.kswallet.core.model.Currency
import com.kswallet.feature.accounts.domain.usecase.CountAccountsUseCase
import com.kswallet.feature.accounts.domain.usecase.ObserveTotalBalanceUseCase
import com.kswallet.feature.home.domain.model.HomeSummary
import com.kswallet.feature.transactions.domain.model.PeriodOption
import com.kswallet.feature.transactions.domain.usecase.ObserveCashFlowUseCase
import com.kswallet.feature.transactions.domain.usecase.ObserveRecentTransactionsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

/**
 * Aggregates the dashboard out of the accounts and transactions features.
 *
 * This is the point of keeping cross-feature dependencies on the domain layer: home needs
 * *data* from two other features and gets it without learning anything about their
 * databases, their ViewModels or their screens.
 */
class ObserveHomeSummaryUseCase(
    private val observeTotalBalance: ObserveTotalBalanceUseCase,
    private val countAccounts: CountAccountsUseCase,
    private val observeCashFlow: ObserveCashFlowUseCase,
    private val observeRecentTransactions: ObserveRecentTransactionsUseCase,
) {
    operator fun invoke(currency: Currency): Flow<HomeSummary> = combine(
        observeTotalBalance(currency),
        observeCashFlow(PeriodOption.THIS_MONTH, currency),
        observeRecentTransactions(RECENT_TRANSACTION_COUNT),
        accountCountFlow(),
    ) { totalBalance, cashFlow, recent, accountCount ->
        HomeSummary(
            totalBalance = totalBalance,
            accountCount = accountCount,
            monthIncome = cashFlow.income,
            monthExpense = cashFlow.expense,
            recentTransactions = recent,
        )
    }

    /**
     * The account count is a one-shot query, but the dashboard combines it with streams, so
     * it is wrapped in a flow instead of being re-queried on every emission.
     */
    private fun accountCountFlow(): Flow<Int> = flow {
        emit(
            when (val count = countAccounts()) {
                is com.kswallet.core.common.result.Outcome.Success -> count.value
                is com.kswallet.core.common.result.Outcome.Failure -> 0
            },
        )
    }

    companion object {
        /**
         * How many entries the dashboard previews. A layout decision, not a dependency:
         * keeping it out of the constructor is what lets Koin's compile-time graph check
         * resolve this use case without a `kotlin.Int` provider.
         */
        private const val RECENT_TRANSACTION_COUNT = 5
    }
}
