package com.kswallet.feature.budgets.data.di

import com.kswallet.feature.budgets.data.repository.OfflineFirstBudgetRepository
import com.kswallet.feature.budgets.domain.repository.BudgetRepository
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

val budgetsDataModule = module {
    single<OfflineFirstBudgetRepository>() bind BudgetRepository::class
}
