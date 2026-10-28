package com.kswallet.app.di

import com.kswallet.core.currencies.createKsWalletCurrencyRegistry
import com.kswallet.core.database.WalletDatabase
import com.kswallet.core.database.WalletDatabaseFactory
import com.kswallet.core.database.account.AccountDao
import com.kswallet.core.money.CurrencyRegistry
import com.kswallet.feature.accounts.data.repository.RoomAccountRepository
import com.kswallet.feature.accounts.domain.repository.AccountRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.dsl.onClose

fun persistenceModule(
    databaseName: String = "kswallet.db",
): Module = module {

    single<WalletDatabase> {
        WalletDatabaseFactory.create(
            context = androidContext(),
            databaseName = databaseName,
        )
    } onClose { database ->
        database?.close()
    }

    single<AccountDao> {
        get<WalletDatabase>().accountDao()
    }

    single<CurrencyRegistry> {
        createKsWalletCurrencyRegistry()
    }

    single<AccountRepository> {
        RoomAccountRepository(
            accountDao = get(),
            currencyRegistry = get(),
        )
    }
}