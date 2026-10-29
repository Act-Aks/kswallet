package com.kswallet.app.di

import com.kswallet.feature.accounts.presentation.di.accountsPresentationModule
import org.koin.core.module.Module

val appModules: List<Module> = listOf(
    foundationModule,
    persistenceModule(),
    accountsPresentationModule,
)