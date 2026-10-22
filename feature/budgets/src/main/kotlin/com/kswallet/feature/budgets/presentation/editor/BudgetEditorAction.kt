package com.kswallet.feature.budgets.presentation.editor

import com.kswallet.core.model.CategoryId
import com.kswallet.core.ui.screen.ScreenAction
import com.kswallet.feature.budgets.domain.model.BudgetPeriod

sealed interface BudgetEditorAction : ScreenAction {
    data class CategorySelected(val id: CategoryId) : BudgetEditorAction
    data class LimitChanged(val text: String) : BudgetEditorAction
    data class PeriodChanged(val period: BudgetPeriod) : BudgetEditorAction
    data object SaveClicked : BudgetEditorAction
    data object BackClicked : BudgetEditorAction
}
