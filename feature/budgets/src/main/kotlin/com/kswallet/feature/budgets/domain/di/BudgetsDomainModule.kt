package com.kswallet.feature.budgets.domain.di

import com.kswallet.feature.budgets.domain.usecase.DeleteBudgetUseCase
import com.kswallet.feature.budgets.domain.usecase.ObserveBaseCurrencyUseCase
import com.kswallet.feature.budgets.domain.usecase.ObserveBudgetUseCase
import com.kswallet.feature.budgets.domain.usecase.ObserveBudgetsUseCase
import com.kswallet.feature.budgets.domain.usecase.SaveBudgetUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val budgetsDomainModule = module {
    factoryOf(::ObserveBudgetsUseCase)
    factoryOf(::ObserveBudgetUseCase)
    factoryOf(::ObserveBaseCurrencyUseCase)
    factoryOf(::SaveBudgetUseCase)
    factoryOf(::DeleteBudgetUseCase)
}
