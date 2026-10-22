package com.kswallet.feature.transactions.domain.di

import com.kswallet.feature.transactions.domain.usecase.DeleteTransactionUseCase
import com.kswallet.feature.transactions.domain.usecase.GetTransactionUseCase
import com.kswallet.feature.transactions.domain.usecase.ObserveCashFlowUseCase
import com.kswallet.feature.transactions.domain.usecase.ObserveRecentTransactionsUseCase
import com.kswallet.feature.transactions.domain.usecase.ObserveTransactionsUseCase
import com.kswallet.feature.transactions.domain.usecase.ResolvePeriodUseCase
import com.kswallet.feature.transactions.domain.usecase.SaveTransactionUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val transactionsDomainModule = module {
    factoryOf(::ResolvePeriodUseCase)
    factoryOf(::ObserveTransactionsUseCase)
    factoryOf(::ObserveRecentTransactionsUseCase)
    factoryOf(::ObserveCashFlowUseCase)
    factoryOf(::GetTransactionUseCase)
    factoryOf(::SaveTransactionUseCase)
    factoryOf(::DeleteTransactionUseCase)
}
