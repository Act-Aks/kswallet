package com.kswallet.feature.budgets.presentation.list

import com.kswallet.core.model.BudgetId
import com.kswallet.core.ui.screen.ScreenAction

sealed interface BudgetsAction : ScreenAction {
    data object AddBudgetClicked : BudgetsAction
    data class BudgetClicked(val id: BudgetId) : BudgetsAction
    data class DeleteClicked(val id: BudgetId) : BudgetsAction
    data object DeleteConfirmed : BudgetsAction
    data object DeleteDismissed : BudgetsAction
}
