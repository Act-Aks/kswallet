package com.kswallet.feature.budgets.presentation.list

import com.kswallet.core.model.BudgetId
import com.kswallet.core.model.Money

/**
 * A row carries values, not text: the category's name and colours come from the domain, the
 * money stays a [com.kswallet.core.model.Money], and the screen decides how all of it is rendered.
 */
data class BudgetRowUi(
    val id: BudgetId,
    val categoryName: String,
    val iconKey: String,
    val colorArgb: Long,
    val periodLabel: BudgetPeriodLabel,
    val limit: Money,
    val spent: Money,
    val fractionUsed: Float,
    val isOverBudget: Boolean,
)
