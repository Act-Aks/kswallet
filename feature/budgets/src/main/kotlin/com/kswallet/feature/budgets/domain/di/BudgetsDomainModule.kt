package com.kswallet.feature.budgets.domain.di

import com.kswallet.core.datastore.domain.repository.SettingsRepository
import com.kswallet.feature.budgets.domain.repository.BudgetRepository
import com.kswallet.feature.budgets.domain.usecase.DeleteBudgetUseCase
import com.kswallet.feature.budgets.domain.usecase.ObserveBaseCurrencyUseCase
import com.kswallet.feature.budgets.domain.usecase.ObserveBudgetUseCase
import com.kswallet.feature.budgets.domain.usecase.ObserveBudgetsUseCase
import com.kswallet.feature.budgets.domain.usecase.SaveBudgetUseCase
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
class BudgetsDomainModule {
    @Factory
    fun provideObserveBudgetsUseCase(
        repository: BudgetRepository,
    ): ObserveBudgetsUseCase =
        ObserveBudgetsUseCase(repository)

    @Factory
    fun provideObserveBudgetUseCase(
        repository: BudgetRepository,
    ): ObserveBudgetUseCase =
        ObserveBudgetUseCase(repository)

    @Factory
    fun provideObserveBaseCurrencyUseCase(
        settingsRepository: SettingsRepository,
    ): ObserveBaseCurrencyUseCase =
        ObserveBaseCurrencyUseCase(settingsRepository)

    @Factory
    fun provideSaveBudgetUseCase(
        repository: BudgetRepository,
    ): SaveBudgetUseCase =
        SaveBudgetUseCase(repository)

    @Factory
    fun provideDeleteBudgetUseCase(
        repository: BudgetRepository,
    ): DeleteBudgetUseCase =
        DeleteBudgetUseCase(repository)
}
