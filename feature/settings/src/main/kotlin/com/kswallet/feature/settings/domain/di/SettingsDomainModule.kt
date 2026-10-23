package com.kswallet.feature.settings.domain.di

import com.kswallet.core.datastore.domain.repository.SettingsRepository
import com.kswallet.feature.settings.domain.usecase.ObserveSettingsUseCase
import com.kswallet.feature.settings.domain.usecase.SetDefaultCurrencyUseCase
import com.kswallet.feature.settings.domain.usecase.SetDynamicColorUseCase
import com.kswallet.feature.settings.domain.usecase.SetThemeModeUseCase
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
class SettingsDomainModule {
    @Factory
    fun provideObserveSettingsUseCase(
        repository: SettingsRepository,
    ): ObserveSettingsUseCase =
        ObserveSettingsUseCase(repository)

    @Factory
    fun provideSetThemeModeUseCase(
        repository: SettingsRepository,
    ): SetThemeModeUseCase =
        SetThemeModeUseCase(repository)

    @Factory
    fun provideSetDynamicColorUseCase(
        repository: SettingsRepository,
    ): SetDynamicColorUseCase =
        SetDynamicColorUseCase(repository)

    @Factory
    fun provideSetDefaultCurrencyUseCase(
        repository: SettingsRepository,
    ): SetDefaultCurrencyUseCase =
        SetDefaultCurrencyUseCase(repository)
}
