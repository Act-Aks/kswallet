package com.kswallet.feature.reports.domain.model

import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.Money

/** One line of "where did the money go": a category, or the uncategorized bucket. */
data class CategoryReportRow(
    val categoryId: CategoryId?,
    val name: String?,
    val iconKey: String?,
    val colorArgb: Long?,
    val amount: Money,
    val transactionCount: Int,
    val share: Float,
) {
    /** Spending the user never filed under a category. The screen labels it itself. */
    val isUncategorised: Boolean get() = categoryId == null
}
