package com.kswallet.feature.transactions.domain.di

import com.kswallet.core.common.time.TimeProvider
import com.kswallet.feature.transactions.domain.repository.TransactionRepository
import com.kswallet.feature.transactions.domain.usecase.DeleteTransactionUseCase
import com.kswallet.feature.transactions.domain.usecase.GetTransactionUseCase
import com.kswallet.feature.transactions.domain.usecase.ObserveCashFlowUseCase
import com.kswallet.feature.transactions.domain.usecase.ObserveRecentTransactionsUseCase
import com.kswallet.feature.transactions.domain.usecase.ObserveTransactionsUseCase
import com.kswallet.feature.transactions.domain.usecase.ResolvePeriodUseCase
import com.kswallet.feature.transactions.domain.usecase.SaveTransactionUseCase
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
class TransactionsDomainModule {
    @Factory
    fun provideResolvePeriodUseCase(
        timeProvider: TimeProvider,
    ): ResolvePeriodUseCase =
        ResolvePeriodUseCase(timeProvider)

    @Factory
    fun provideObserveTransactionsUseCase(
        repository: TransactionRepository,
        resolvePeriod: ResolvePeriodUseCase,
    ): ObserveTransactionsUseCase =
        ObserveTransactionsUseCase(repository, resolvePeriod)

    @Factory
    fun provideObserveRecentTransactionsUseCase(
        repository: TransactionRepository,
    ): ObserveRecentTransactionsUseCase =
        ObserveRecentTransactionsUseCase(repository)

    @Factory
    fun provideObserveCashFlowUseCase(
        repository: TransactionRepository,
        resolvePeriod: ResolvePeriodUseCase,
    ): ObserveCashFlowUseCase =
        ObserveCashFlowUseCase(repository, resolvePeriod)

    @Factory
    fun provideGetTransactionUseCase(
        repository: TransactionRepository,
    ): GetTransactionUseCase =
        GetTransactionUseCase(repository)

    @Factory
    fun provideSaveTransactionUseCase(
        repository: TransactionRepository,
        timeProvider: TimeProvider,
    ): SaveTransactionUseCase =
        SaveTransactionUseCase(repository, timeProvider)

    @Factory
    fun provideDeleteTransactionUseCase(
        repository: TransactionRepository,
    ): DeleteTransactionUseCase =
        DeleteTransactionUseCase(repository)
}
