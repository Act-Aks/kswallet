package com.kswallet.core.database

import androidx.room3.AutoMigration
import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.kswallet.core.database.dao.AccountDao
import com.kswallet.core.database.dao.BudgetDao
import com.kswallet.core.database.dao.CategoryDao
import com.kswallet.core.database.dao.TransactionDao
import com.kswallet.core.database.entity.AccountEntity
import com.kswallet.core.database.entity.BudgetEntity
import com.kswallet.core.database.entity.CategoryEntity
import com.kswallet.core.database.entity.TransactionEntity

/**
 * The single Room database of the app.
 *
 * Room 3 notes that shape this file:
 *  - the package is `androidx.room3` (Room 3 lives beside Room 2.x so that libraries
 *    still on 2.x can coexist),
 *  - code generation is Kotlin-only and KSP-only,
 *  - DAO functions are either `suspend` or return `Flow` - blocking calls no longer exist.
 *
 * Entities and DAOs live in this module while the repositories that use them live in the
 * feature modules: the schema is a shared artifact, the persistence *logic* stays with
 * the feature that owns the data.
 *
 * Version 2 adds `budgets`. Because that release only *adds* a table and an index, it is an
 * `@AutoMigration`: Room derives the statements from the two exported schemas (`schemas/1.json`
 * and `schemas/2.json`) and verifies the result at compile time, so there is no hand-written
 * SQL to get wrong. A migration that rewrites data would go into [WalletMigrations.ALL].
 */
@Database(
    entities = [
        AccountEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        BudgetEntity::class,
    ],
    version = WalletDatabase.VERSION,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
    ],
)
abstract class WalletDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao

    abstract fun categoryDao(): CategoryDao

    abstract fun transactionDao(): TransactionDao

    abstract fun budgetDao(): BudgetDao

    companion object {
        const val VERSION = 2
        const val NAME = "kswallet.db"
    }
}
