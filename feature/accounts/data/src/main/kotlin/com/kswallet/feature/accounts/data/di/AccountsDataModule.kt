package com.kswallet.feature.accounts.data.di

import com.kswallet.feature.accounts.data.repository.AccountRepositoryImpl
import com.kswallet.feature.accounts.domain.repository.AccountRepository
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

val accountsDataModule = module {
    single<AccountRepositoryImpl>() bind AccountRepository::class
}