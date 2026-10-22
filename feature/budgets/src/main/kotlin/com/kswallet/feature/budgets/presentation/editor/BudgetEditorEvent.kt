package com.kswallet.feature.budgets.presentation.editor

import com.kswallet.core.ui.screen.ScreenEvent
import com.kswallet.core.ui.util.UiMessage

sealed interface BudgetEditorEvent : ScreenEvent {
    data class NavigateBack(val saved: Boolean = false) : BudgetEditorEvent
    data class ShowMessage(val message: UiMessage) : BudgetEditorEvent
}
