package com.kswallet.feature.settings.domain.di

import com.kswallet.feature.settings.domain.usecase.ObserveSettingsUseCase
import com.kswallet.feature.settings.domain.usecase.SetDefaultCurrencyUseCase
import com.kswallet.feature.settings.domain.usecase.SetDynamicColorUseCase
import com.kswallet.feature.settings.domain.usecase.SetThemeModeUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val settingsDomainModule = module {
    factoryOf(::ObserveSettingsUseCase)
    factoryOf(::SetThemeModeUseCase)
    factoryOf(::SetDynamicColorUseCase)
    factoryOf(::SetDefaultCurrencyUseCase)
}
