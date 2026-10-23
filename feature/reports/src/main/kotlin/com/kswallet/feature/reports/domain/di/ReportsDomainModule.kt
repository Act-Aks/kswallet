package com.kswallet.feature.reports.domain.di

import com.kswallet.core.datastore.domain.repository.SettingsRepository
import com.kswallet.feature.reports.domain.repository.ReportRepository
import com.kswallet.feature.reports.domain.usecase.ObserveBaseCurrencyUseCase
import com.kswallet.feature.reports.domain.usecase.ObserveMonthlyReportUseCase
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
class ReportsDomainModule {
    @Factory
    fun provideObserveBaseCurrencyUseCase(
        settingsRepository: SettingsRepository,
    ): ObserveBaseCurrencyUseCase =
        ObserveBaseCurrencyUseCase(settingsRepository)

    @Factory
    fun provideObserveMonthlyReportUseCase(
        repository: ReportRepository,
    ): ObserveMonthlyReportUseCase =
        ObserveMonthlyReportUseCase(repository)
}
