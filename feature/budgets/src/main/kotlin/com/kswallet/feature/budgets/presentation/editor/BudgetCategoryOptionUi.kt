package com.kswallet.feature.budgets.presentation.editor

import com.kswallet.core.model.CategoryId

data class BudgetCategoryOptionUi(
    val id: CategoryId,
    val name: String,
    val iconKey: String,
)
