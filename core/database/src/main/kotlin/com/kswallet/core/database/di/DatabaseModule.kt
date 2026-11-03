package com.kswallet.core.database.di

import com.kswallet.core.database.WalletDatabase
import com.kswallet.core.database.WalletDatabaseFactory
import com.kswallet.core.database.dao.AccountDao
import com.kswallet.core.database.dao.TransactionDao
import org.koin.dsl.module
import org.koin.dsl.onClose

val databaseModule = module {
    
    single<WalletDatabase> {
        WalletDatabaseFactory.create(context = get())
    } onClose { database ->
        database?.close()
    }

    single<AccountDao> { get<WalletDatabase>().accountDao() }
    single<TransactionDao> { get<WalletDatabase>().transactionDao() }
}