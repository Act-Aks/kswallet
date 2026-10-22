package com.kswallet.feature.home.domain.di

import com.kswallet.feature.home.domain.usecase.ObserveHomeSummaryUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val homeDomainModule = module {
    factoryOf(::ObserveHomeSummaryUseCase)
}
