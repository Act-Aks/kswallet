package com.kswallet.core.database

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

object WalletDatabaseFactory {

    private const val DATABASE_NAME = "kswallet.db"

    fun create(context: Context): WalletDatabase {
        val appContext = context.applicationContext
        val databaseFile = appContext.getDatabasePath(DATABASE_NAME)

        return Room.databaseBuilder<WalletDatabase>(
            context = appContext,
            name = databaseFile.absolutePath,
            factory = { WalletDatabase_Impl() },
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    internal fun createInMemory(): WalletDatabase =
        Room.inMemoryDatabaseBuilder<WalletDatabase>(
            factory = { WalletDatabase_Impl() },
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
}