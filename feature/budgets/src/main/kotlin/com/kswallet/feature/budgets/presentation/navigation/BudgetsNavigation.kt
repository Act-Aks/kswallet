package com.kswallet.feature.budgets.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.kswallet.core.navigation.KsWalletDestination
import com.kswallet.feature.budgets.presentation.editor.BudgetEditorRoute
import com.kswallet.feature.budgets.presentation.list.BudgetsRoute
import kotlinx.serialization.Serializable

/** Destinations owned by the budgets feature. */
@Serializable
data object BudgetsDestination : KsWalletDestination

@Serializable
data class BudgetEditorDestination(val budgetId: Long? = null) : KsWalletDestination

fun NavGraphBuilder.budgetsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEditor: (budgetId: Long?) -> Unit,
) {
    composable<BudgetsDestination> {
        BudgetsRoute(
            onAddBudget = { onNavigateToEditor(null) },
            onEditBudget = { id -> onNavigateToEditor(id.value) },
        )
    }

    composable<BudgetEditorDestination> { backStackEntry ->
        val destination = backStackEntry.toRoute<BudgetEditorDestination>()
        BudgetEditorRoute(
            budgetIdValue = destination.budgetId ?: 0L,
            onNavigateBack = onNavigateBack,
        )
    }
}
