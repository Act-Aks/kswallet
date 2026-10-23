package com.kswallet.feature.accounts.domain.di

import com.kswallet.core.common.time.TimeProvider
import com.kswallet.feature.accounts.domain.repository.AccountRepository
import com.kswallet.feature.accounts.domain.usecase.CountAccountsUseCase
import com.kswallet.feature.accounts.domain.usecase.DeleteAccountUseCase
import com.kswallet.feature.accounts.domain.usecase.GetAccountTransactionCountUseCase
import com.kswallet.feature.accounts.domain.usecase.GetAccountUseCase
import com.kswallet.feature.accounts.domain.usecase.ObserveAccountUseCase
import com.kswallet.feature.accounts.domain.usecase.ObserveAccountsUseCase
import com.kswallet.feature.accounts.domain.usecase.ObserveForeignCurrencyAccountsUseCase
import com.kswallet.feature.accounts.domain.usecase.ObserveTotalBalanceUseCase
import com.kswallet.feature.accounts.domain.usecase.SaveAccountUseCase
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
class AccountsDomainModule {
    @Factory
    fun provideObserveAccountsUseCase(
        repository: AccountRepository,
    ): ObserveAccountsUseCase =
        ObserveAccountsUseCase(repository)

    @Factory
    fun provideObserveAccountUseCase(
        repository: AccountRepository,
    ): ObserveAccountUseCase =
        ObserveAccountUseCase(repository)

    @Factory
    fun provideObserveTotalBalanceUseCase(
        repository: AccountRepository,
    ): ObserveTotalBalanceUseCase =
        ObserveTotalBalanceUseCase(repository)

    @Factory
    fun provideObserveForeignCurrencyAccountsUseCase(
        repository: AccountRepository,
    ): ObserveForeignCurrencyAccountsUseCase =
        ObserveForeignCurrencyAccountsUseCase(repository)

    @Factory
    fun provideGetAccountUseCase(
        repository: AccountRepository,
    ): GetAccountUseCase =
        GetAccountUseCase(repository)

    @Factory
    fun provideGetAccountTransactionCountUseCase(
        repository: AccountRepository,
    ): GetAccountTransactionCountUseCase =
        GetAccountTransactionCountUseCase(repository)

    @Factory
    fun provideSaveAccountUseCase(
        repository: AccountRepository,
        timeProvider: TimeProvider,
    ): SaveAccountUseCase =
        SaveAccountUseCase(repository, timeProvider)

    @Factory
    fun provideDeleteAccountUseCase(
        repository: AccountRepository,
    ): DeleteAccountUseCase =
        DeleteAccountUseCase(repository)

    @Factory
    fun provideCountAccountsUseCase(
        repository: AccountRepository,
    ): CountAccountsUseCase =
        CountAccountsUseCase(repository)
}
