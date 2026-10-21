package com.kswallet.core.database.model

import androidx.room3.ColumnInfo
import androidx.room3.Embedded
import com.kswallet.core.database.entity.BudgetEntity

/** A budget joined with the category it limits. */
data class BudgetWithCategoryRow(
    @Embedded
    val budget: BudgetEntity,

    @ColumnInfo(name = "category_name")
    val categoryName: String,

    @ColumnInfo(name = "category_icon_key")
    val categoryIconKey: String,

    @ColumnInfo(name = "category_color_argb")
    val categoryColorArgb: Long,
)
