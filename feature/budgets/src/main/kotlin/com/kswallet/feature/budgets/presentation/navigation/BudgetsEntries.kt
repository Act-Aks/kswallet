package com.kswallet.feature.budgets.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kswallet.feature.budgets.presentation.editor.BudgetEditorRoot
import com.kswallet.feature.budgets.presentation.list.BudgetsRoot

fun EntryProviderScope<NavKey>.budgetsEntries(
    onNavigateBack: () -> Unit,
    onNavigateToEditor: (budgetId: Long?) -> Unit,
) {
    entry<BudgetsRoute> {
        BudgetsRoot(
            onAddBudget = { onNavigateToEditor(null) },
            onEditBudget = { id -> onNavigateToEditor(id.value) },
        )
    }

    entry<BudgetEditorRoute> { destination ->
        BudgetEditorRoot(
            budgetIdValue = destination.budgetId ?: 0L,
            onNavigateBack = onNavigateBack,
        )
    }
}
