package com.kswallet.app.di

import com.kswallet.core.currencies.di.currenciesModule
import com.kswallet.core.database.di.databaseModule
import com.kswallet.feature.accounts.data.di.accountsDataModule
import com.kswallet.feature.accounts.presentation.di.accountsPresentationModule
import org.koin.core.module.Module

val appModules: List<Module> = listOf(
    // core modules
    currenciesModule,
    databaseModule,

    // feature modules
    accountsDataModule,
    accountsPresentationModule,
)