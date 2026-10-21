package com.kswallet.core.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

/**
 * A spending limit for one category.
 *
 * Design decisions the schema encodes:
 *  - **One budget per category and period** (the unique index). Two monthly budgets for
 *    "Groceries" would be ambiguous, and the screen has no way to tell the user which one
 *    their spending counts against.
 *  - **Deleting a category deletes its budget** (CASCADE): a limit for a category that no
 *    longer exists cannot mean anything, and the alternative (a dangling row) would show up
 *    as a nameless progress bar.
 *  - **The limit is stored in minor units plus its currency**, like every other amount in
 *    the app; the currency cannot be derived from the category, so it travels with the row.
 */
@Entity(
    tableName = "budgets",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["category_id", "period"], unique = true)],
)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "category_id")
    val categoryId: Long,

    @ColumnInfo(name = "limit_minor")
    val limitMinor: Long,

    @ColumnInfo(name = "currency_code")
    val currencyCode: String,

    @ColumnInfo(name = "period")
    val period: String,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
)
