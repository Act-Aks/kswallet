package com.kswallet.core.currencies.di

import com.kswallet.core.currencies.createKsWalletCurrencyRegistry
import com.kswallet.core.money.CurrencyRegistry
import org.koin.dsl.module

val currenciesModule = module {
    single<CurrencyRegistry> { createKsWalletCurrencyRegistry() }
}