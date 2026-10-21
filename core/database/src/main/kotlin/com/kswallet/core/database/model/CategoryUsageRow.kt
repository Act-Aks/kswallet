package com.kswallet.core.database.model

import androidx.room3.ColumnInfo

/** Sum of a category's transactions, used to warn before deleting it. */
data class CategoryUsageRow(
    @ColumnInfo(name = "category_id")
    val categoryId: Long,

    @ColumnInfo(name = "transaction_count")
    val transactionCount: Int,
)
