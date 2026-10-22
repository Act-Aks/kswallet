package com.kswallet.feature.budgets.presentation.list

import com.kswallet.core.ui.screen.ScreenState

data class BudgetsState(
    val isLoading: Boolean = true,
    val budgets: List<BudgetRowUi> = emptyList(),
    val pendingDelete: PendingBudgetDelete? = null,
) : ScreenState {
    val isEmpty: Boolean get() = !isLoading && budgets.isEmpty()
}

