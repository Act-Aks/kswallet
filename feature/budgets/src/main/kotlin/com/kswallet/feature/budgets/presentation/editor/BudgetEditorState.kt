package com.kswallet.feature.budgets.presentation.editor

import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.Currency
import com.kswallet.core.ui.screen.ScreenState
import com.kswallet.feature.budgets.domain.model.BudgetPeriod

data class BudgetEditorState(
    val isLoading: Boolean = true,
    val isNew: Boolean = true,
    val currency: Currency = Currency.Default,
    val categoryId: CategoryId? = null,
    val limitText: String = "",
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
    /** Expense categories only: a limit on income makes no sense. */
    val categories: List<BudgetCategoryOptionUi> = emptyList(),
    val error: BudgetEditorError? = null,
    val isSaving: Boolean = false,
) : ScreenState {
    val categoryName: String?
        get() = categories.firstOrNull { it.id == categoryId }?.name
}
