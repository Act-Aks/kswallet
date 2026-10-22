package com.kswallet.feature.reports.presentation.reports

import com.kswallet.core.model.Money

/** One row of "where it went". [categoryId] is null for the uncategorised bucket. */
data class CategoryShareUi(
    val categoryId: Long?,
    val name: String?,
    val iconKey: String?,
    val colorArgb: Long?,
    val amount: Money,
    val share: Float,
    val transactionCount: Int,
)
