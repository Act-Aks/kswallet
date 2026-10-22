package com.kswallet.feature.budgets.domain.usecase

import com.kswallet.feature.budgets.domain.model.BudgetProgress
import com.kswallet.feature.budgets.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow

class ObserveBudgetsUseCase(
    private val repository: BudgetRepository,
) {
    operator fun invoke(): Flow<List<BudgetProgress>> = repository.observeBudgets()
}
