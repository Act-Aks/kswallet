package com.kswallet.core.database

import androidx.room3.AutoMigration
import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.kswallet.core.database.dao.AccountDao
import com.kswallet.core.database.dao.TransactionDao
import com.kswallet.core.database.entity.AccountEntity
import com.kswallet.core.database.entity.TransactionEntity

/**
 * The single Room database of the app.
 *
 * ## Schema ownership
 *
 * Entities and DAOs live in this module because the database schema is a shared
 * artifact. Feature data modules own only the mapping between these entities and
 * their feature-specific domain models, plus the repository implementations that
 * use the DAOs.
 *
 * ## Migration workflow
 *
 * When changing the database schema:
 *
 * 1. Change or add an entity.
 * 2. Set [PREVIOUS_VERSION] to the current value of [VERSION].
 * 3. Increase [VERSION] by one.
 * 4. Build the database module so Room exports the new schema JSON file.
 * 5. Add an `AutoMigration` for the newly completed version pair.
 * 6. Add a manual migration to [com.kswallet.core.database.migration.WalletMigrations.ALL] only when Room cannot infer
 *    the change, such as renaming a column, copying data, backfilling values, or
 *    splitting or merging tables.
 * 7. Commit both the old and new exported schema files.
 *
 * ## Auto-migrations
 *
 * An `AutoMigration` is appropriate when the schema change is structural and does
 * not require a decision about existing data—for example, adding a table, adding a
 * nullable column, or adding an index. Room generates the migration from the
 * exported schemas for the two versions.
 *
 * The final entry in `autoMigrations` is driven by [PREVIOUS_VERSION] and
 * [VERSION]. When a newer schema version is introduced, that entry automatically
 * describes the newest version pair; the previously latest pair must then be
 * frozen as an explicit `AutoMigration`.
 *
 * ## Manual migrations
 *
 * Manual migrations live in [com.kswallet.core.database.migration.WalletMigrations.ALL]. They are required when existing
 * rows must be transformed, because Room cannot infer the intended mapping. Room 3
 * manual migrations receive a `SQLiteConnection` and execute SQL directly.
 *
 * Example:
 *
 * ```kotlin
 * val MIGRATION_2_3 = object : Migration(2, 3) {
 *     override fun migrate(connection: SQLiteConnection) {
 *         connection.execSQL("ALTER TABLE transactions ADD COLUMN category_id TEXT")
 *     }
 * }
 * ```
 */
@Database(
    entities = [
        AccountEntity::class,
        TransactionEntity::class,
    ],
    version = WalletDatabase.VERSION,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(
            from = WalletDatabase.PREVIOUS_VERSION,
            to = WalletDatabase.VERSION,
        ),
    ],
)
abstract class WalletDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao

    companion object {

        /**
         * The database version immediately before [VERSION].
         *
         * When introducing a new schema version, set this to the previous value
         * of [VERSION].
         */
        const val PREVIOUS_VERSION = 1

        /**
         * The current database schema version.
         *
         * Increase by one for every schema-changing release. Never decrease this
         * value.
         */
        const val VERSION = 2
    }
}