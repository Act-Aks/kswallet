package com.kswallet.core.database

import androidx.room3.migration.Migration

/**
 * Handwritten migrations, applied in order at open time.
 *
 * Workflow for a schema change:
 *  1. bump [WalletDatabase.VERSION],
 *  2. change the entity,
 *  3. build once - Room writes `schemas/<version>.json` (checked into git),
 *  4. add the `Migration` here (or an `@AutoMigration` when only tables/columns were added),
 *  5. add a test that opens the old schema and migrates it.
 *
 *  - 1 -> 2: `budgets` was added, declared as an `@AutoMigration` on [WalletDatabase].
 *    Auto-migrations are checked against the exported schemas at compile time, which is why
 *    this list has nothing to say about it - a migration only belongs here when rows have to
 *    be rewritten (a renamed column, a split table, a backfill), because then no algorithm
 *    can guess the intent.
 */
object WalletMigrations {
    val ALL: Array<Migration> = emptyArray()
}
