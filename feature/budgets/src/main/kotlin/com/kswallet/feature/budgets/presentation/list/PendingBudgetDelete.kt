package com.kswallet.feature.budgets.presentation.list

import com.kswallet.core.model.BudgetId

data class PendingBudgetDelete(
    val id: BudgetId,
    val categoryName: String,
)
