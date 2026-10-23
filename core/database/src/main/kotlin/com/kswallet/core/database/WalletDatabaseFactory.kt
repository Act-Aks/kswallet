package com.kswallet.core.database

import org.koin.core.annotation.Single
import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

/**
 * Builds the Room 3 database.
 *
 * Room 3 is a full [androidx.sqlite] driver-API database (SupportSQLite is gone), so the
 * SQLite engine is bundled with the app instead of being whatever the device ships. That is
 * what makes "works on my phone" also mean "works on every phone", and it is the only
 * supported way to get identical query behaviour across Android versions.
 */
@Single
class WalletDatabaseFactory(private val context: Context) {

    fun create(): WalletDatabase =
        Room.databaseBuilder<WalletDatabase>(
            context = context.applicationContext,
            name = context.applicationContext.getDatabasePath(WalletDatabase.NAME).absolutePath,
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .addMigrations(*WalletMigrations.ALL)
            .build()
}
