package com.kswallet.feature.home.domain.di

import com.kswallet.feature.accounts.domain.usecase.CountAccountsUseCase
import com.kswallet.feature.accounts.domain.usecase.ObserveTotalBalanceUseCase
import com.kswallet.feature.home.domain.usecase.ObserveHomeSummaryUseCase
import com.kswallet.feature.transactions.domain.usecase.ObserveCashFlowUseCase
import com.kswallet.feature.transactions.domain.usecase.ObserveRecentTransactionsUseCase
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
class HomeDomainModule {
    @Factory
    fun provideObserveHomeSummaryUseCase(
        observeTotalBalance: ObserveTotalBalanceUseCase,
        countAccounts: CountAccountsUseCase,
        observeCashFlow: ObserveCashFlowUseCase,
        observeRecentTransactions: ObserveRecentTransactionsUseCase,
    ): ObserveHomeSummaryUseCase =
        ObserveHomeSummaryUseCase(observeTotalBalance, countAccounts, observeCashFlow, observeRecentTransactions)
}
