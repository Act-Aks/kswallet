package com.kswallet.feature.budgets.domain.validation

import com.kswallet.core.model.CategoryId
import com.kswallet.feature.budgets.domain.model.BudgetPeriod

data class BudgetFormInput(
    val categoryId: CategoryId?,
    val limitText: String,
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
)
