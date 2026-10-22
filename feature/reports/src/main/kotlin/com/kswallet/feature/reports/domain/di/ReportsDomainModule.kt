package com.kswallet.feature.reports.domain.di

import com.kswallet.feature.reports.domain.usecase.ObserveBaseCurrencyUseCase
import com.kswallet.feature.reports.domain.usecase.ObserveMonthlyReportUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val reportsDomainModule = module {
    factoryOf(::ObserveBaseCurrencyUseCase)
    factoryOf(::ObserveMonthlyReportUseCase)
}
