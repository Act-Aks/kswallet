package com.kswallet.feature.accounts.domain.di

import com.kswallet.feature.accounts.domain.usecase.CountAccountsUseCase
import com.kswallet.feature.accounts.domain.usecase.DeleteAccountUseCase
import com.kswallet.feature.accounts.domain.usecase.GetAccountTransactionCountUseCase
import com.kswallet.feature.accounts.domain.usecase.GetAccountUseCase
import com.kswallet.feature.accounts.domain.usecase.ObserveAccountUseCase
import com.kswallet.feature.accounts.domain.usecase.ObserveAccountsUseCase
import com.kswallet.feature.accounts.domain.usecase.ObserveForeignCurrencyAccountsUseCase
import com.kswallet.feature.accounts.domain.usecase.ObserveTotalBalanceUseCase
import com.kswallet.feature.accounts.domain.usecase.SaveAccountUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val accountsDomainModule = module {
    factoryOf(::ObserveAccountsUseCase)
    factoryOf(::ObserveAccountUseCase)
    factoryOf(::ObserveTotalBalanceUseCase)
    factoryOf(::ObserveForeignCurrencyAccountsUseCase)
    factoryOf(::GetAccountUseCase)
    factoryOf(::GetAccountTransactionCountUseCase)
    factoryOf(::SaveAccountUseCase)
    factoryOf(::DeleteAccountUseCase)
    factoryOf(::CountAccountsUseCase)
}
