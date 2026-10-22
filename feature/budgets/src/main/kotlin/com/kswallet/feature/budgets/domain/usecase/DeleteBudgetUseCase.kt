package com.kswallet.feature.budgets.domain.usecase

import com.kswallet.core.common.result.Outcome
import com.kswallet.core.model.BudgetId
import com.kswallet.feature.budgets.domain.repository.BudgetRepository

class DeleteBudgetUseCase(
    private val repository: BudgetRepository,
) {
    suspend operator fun invoke(budgetId: BudgetId): Outcome<Unit> = repository.deleteBudget(budgetId)
}
