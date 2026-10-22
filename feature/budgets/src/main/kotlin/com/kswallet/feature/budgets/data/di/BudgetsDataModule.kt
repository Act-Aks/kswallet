package com.kswallet.feature.budgets.data.di

import com.kswallet.feature.budgets.data.repository.OfflineFirstBudgetRepository
import com.kswallet.feature.budgets.domain.repository.BudgetRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val budgetsDataModule = module {
    singleOf(::OfflineFirstBudgetRepository) bind BudgetRepository::class
}
