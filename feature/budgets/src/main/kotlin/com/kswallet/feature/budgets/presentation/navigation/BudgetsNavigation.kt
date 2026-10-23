package com.kswallet.feature.budgets.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kswallet.core.navigation.KsWalletDestination
import com.kswallet.feature.budgets.presentation.editor.BudgetEditorRoute
import com.kswallet.feature.budgets.presentation.list.BudgetsRoute
import kotlinx.serialization.Serializable

/** Destinations owned by the budgets feature. */
@Serializable
data object BudgetsDestination : KsWalletDestination

@Serializable
data class BudgetEditorDestination(val budgetId: Long? = null) : KsWalletDestination

fun EntryProviderScope<NavKey>.budgetsEntries(
    onNavigateBack: () -> Unit,
    onNavigateToEditor: (budgetId: Long?) -> Unit,
) {
    entry<BudgetsDestination> {
        BudgetsRoute(
            onAddBudget = { onNavigateToEditor(null) },
            onEditBudget = { id -> onNavigateToEditor(id.value) },
        )
    }

    entry<BudgetEditorDestination> { destination ->
        BudgetEditorRoute(
            budgetIdValue = destination.budgetId ?: 0L,
            onNavigateBack = onNavigateBack,
        )
    }
}
