package com.kswallet.feature.budgets.presentation.list

import com.kswallet.core.model.BudgetId
import com.kswallet.core.ui.screen.ScreenEvent
import com.kswallet.core.ui.util.UiMessage

sealed interface BudgetsEvent : ScreenEvent {
    data class OpenEditor(val budgetId: BudgetId?) : BudgetsEvent
    data class ShowMessage(val message: UiMessage) : BudgetsEvent
}
