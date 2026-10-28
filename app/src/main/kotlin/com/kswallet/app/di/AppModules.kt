package com.kswallet.app.di

import org.koin.core.module.Module

val appModules: List<Module> = listOf(
    foundationModule,
    persistenceModule()
)