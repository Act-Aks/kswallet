package com.kswallet.core.database.model

import androidx.room3.ColumnInfo

/**
 * Expenses of one category in one window, with everything a report row needs to render.
 *
 * Unlike [CategorySpendRow] this one is left-joined: transactions without a category are
 * reported as well (as category id `0`), because "where did the money go" has to add up to
 * the total, and an uncategorized expense is still spending.
 */
data class CategorySpendWithDetailsRow(
    @ColumnInfo(name = "category_id")
    val categoryId: Long,

    @ColumnInfo(name = "category_name")
    val categoryName: String?,

    @ColumnInfo(name = "category_icon_key")
    val categoryIconKey: String?,

    @ColumnInfo(name = "category_color_argb")
    val categoryColorArgb: Long?,

    @ColumnInfo(name = "transaction_count")
    val transactionCount: Int,

    @ColumnInfo(name = "spent_minor")
    val spentMinor: Long,
)
