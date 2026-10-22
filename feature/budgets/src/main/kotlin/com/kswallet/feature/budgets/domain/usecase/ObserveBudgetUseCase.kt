package com.kswallet.feature.budgets.domain.usecase

import com.kswallet.core.model.BudgetId
import com.kswallet.feature.budgets.domain.model.Budget
import com.kswallet.feature.budgets.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow

/** The editor needs one budget, and needs it to react to changes: `Flow`, not `suspend`. */
class ObserveBudgetUseCase(
    private val repository: BudgetRepository,
) {
    operator fun invoke(budgetId: BudgetId): Flow<Budget?> = repository.observeBudget(budgetId)
}
