package com.kswallet.core.database.model

import androidx.room3.ColumnInfo

/** Expenses of one category in one window, per currency. See `BudgetDao.observeCategorySpend`. */
data class CategorySpendRow(
    @ColumnInfo(name = "category_id")
    val categoryId: Long,

    @ColumnInfo(name = "currency_code")
    val currencyCode: String,

    @ColumnInfo(name = "spent_minor")
    val spentMinor: Long,
)
