package com.kswallet.core.database

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.kswallet.core.database.account.AccountDao
import com.kswallet.core.database.account.AccountEntity

@Database(
    entities = [
        AccountEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class WalletDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
}