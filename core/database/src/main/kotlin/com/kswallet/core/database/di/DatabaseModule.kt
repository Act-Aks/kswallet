package com.kswallet.core.database.di

import com.kswallet.core.database.WalletDatabase
import com.kswallet.core.database.WalletDatabaseFactory
import com.kswallet.core.database.dao.AccountDao
import com.kswallet.core.database.dao.BudgetDao
import com.kswallet.core.database.dao.CategoryDao
import com.kswallet.core.database.dao.TransactionDao
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

/** Room database and DAO providers. */
@Module
@ComponentScan("com.kswallet.core.database")
class DatabaseModule {
    @Single
    fun provideWalletDatabase(factory: WalletDatabaseFactory): WalletDatabase = factory.create()

    @Single
    fun provideAccountDao(database: WalletDatabase): AccountDao = database.accountDao()

    @Single
    fun provideCategoryDao(database: WalletDatabase): CategoryDao = database.categoryDao()

    @Single
    fun provideTransactionDao(database: WalletDatabase): TransactionDao = database.transactionDao()

    @Single
    fun provideBudgetDao(database: WalletDatabase): BudgetDao = database.budgetDao()
}
