package com.kswallet.core.database.di

import com.kswallet.core.database.WalletDatabase
import com.kswallet.core.database.WalletDatabaseFactory
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

/**
 * Koin definitions for the storage layer.
 *
 * `singleOf(::WalletDatabaseFactory)` is resolved by the Koin compiler plugin at build
 * time, so a missing `Context` provider fails compilation instead of the first query.
 * Everything is lazy: Koin creates the database on first injection, and `single` keeps
 * exactly one instance for the whole app.
 */
val databaseModule = module {
    single<WalletDatabaseFactory>()
    single<WalletDatabase> { get<WalletDatabaseFactory>().create() }

    single { get<WalletDatabase>().accountDao() }
    single { get<WalletDatabase>().categoryDao() }
    single { get<WalletDatabase>().transactionDao() }
    single { get<WalletDatabase>().budgetDao() }
}
