package com.kswallet.feature.budgets.presentation.list

import androidx.lifecycle.viewModelScope
import com.kswallet.core.common.result.Outcome
import com.kswallet.core.mvi.MviViewModel
import com.kswallet.core.mvi.UiMessage
import com.kswallet.core.model.BudgetId
import com.kswallet.feature.budgets.R
import com.kswallet.feature.budgets.domain.model.BudgetPeriod
import com.kswallet.feature.budgets.domain.usecase.DeleteBudgetUseCase
import com.kswallet.feature.budgets.domain.usecase.ObserveBudgetsUseCase
import com.kswallet.feature.budgets.presentation.error.toUiMessage
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * The budgets screen.
 *
 * The list is a projection of one stream (budget + category + spend, combined in the
 * repository); deleting asks for confirmation first and reports the outcome, like the
 * categories and accounts screens do.
 */
class BudgetsViewModel(
    observeBudgets: ObserveBudgetsUseCase,
    private val deleteBudget: DeleteBudgetUseCase,
) : MviViewModel<BudgetsUiState, BudgetsIntent, BudgetsEffect>(BudgetsUiState()) {

    init {
        observeBudgets()
            .onEach { progress ->
                setState {
                    copy(
                        isLoading = false,
                        budgets = progress.map { item ->
                            BudgetRowUi(
                                id = item.budget.id,
                                categoryName = item.category.name,
                                iconKey = item.category.iconKey,
                                colorArgb = item.category.colorArgb,
                                periodLabel = item.budget.period.toLabel(),
                                limit = item.budget.limit,
                                spent = item.spent,
                                fractionUsed = item.fractionUsed,
                                isOverBudget = item.isOverBudget,
                            )
                        },
                        // A budget deleted elsewhere must not leave a dialog behind.
                        pendingDelete = pendingDelete?.takeIf { pending ->
                            progress.any { it.budget.id == pending.id }
                        },
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onIntent(intent: BudgetsIntent) {
        when (intent) {
            BudgetsIntent.AddBudgetClicked -> emitEffect(BudgetsEffect.OpenEditor(null))

            is BudgetsIntent.BudgetClicked -> emitEffect(BudgetsEffect.OpenEditor(intent.id))

            is BudgetsIntent.DeleteClicked -> {
                val row = currentState.budgets.firstOrNull { it.id == intent.id } ?: return
                setState { copy(pendingDelete = PendingBudgetDelete(row.id, row.categoryName)) }
            }

            BudgetsIntent.DeleteConfirmed -> {
                val pending = currentState.pendingDelete ?: return
                setState { copy(pendingDelete = null) }
                viewModelScope.launch { confirmDelete(pending) }
            }

            BudgetsIntent.DeleteDismissed -> setState { copy(pendingDelete = null) }
        }
    }

    private suspend fun confirmDelete(pending: PendingBudgetDelete) {
        when (val result = deleteBudget(pending.id)) {
            is Outcome.Success -> emitEffect(
                BudgetsEffect.ShowMessage(
                    UiMessage.Resource(R.string.budget_deleted, listOf(pending.categoryName)),
                ),
            )

            is Outcome.Failure -> emitEffect(BudgetsEffect.ShowMessage(result.error.toUiMessage()))
        }
    }

    private fun BudgetPeriod.toLabel(): BudgetPeriodLabel = when (this) {
        BudgetPeriod.WEEKLY -> BudgetPeriodLabel.WEEKLY
        BudgetPeriod.MONTHLY -> BudgetPeriodLabel.MONTHLY
        BudgetPeriod.YEARLY -> BudgetPeriodLabel.YEARLY
    }
}
