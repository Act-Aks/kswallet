package com.kswallet.core.database

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

object WalletDatabaseFactory {

    private const val DEFAULT_DATABASE_NAME = "kswallet.db"

    private val validDatabaseName =
        Regex("[A-Za-z0-9][A-Za-z0-9_.-]*")

    fun create(
        context: Context,
        databaseName: String = DEFAULT_DATABASE_NAME,
    ): WalletDatabase {
        require(validDatabaseName.matches(databaseName)) {
            "Database name must be a simple filename."
        }

        val appContext = context.applicationContext
        val databaseFile = appContext.getDatabasePath(databaseName)

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