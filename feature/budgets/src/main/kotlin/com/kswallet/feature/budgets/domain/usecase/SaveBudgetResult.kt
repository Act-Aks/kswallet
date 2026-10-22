package com.kswallet.feature.budgets.domain.usecase

import com.kswallet.core.common.result.DomainError
import com.kswallet.core.model.BudgetId
import com.kswallet.feature.budgets.domain.validation.BudgetFormError

sealed interface SaveBudgetResult {
    data class Saved(val id: BudgetId) : SaveBudgetResult
    data class Invalid(val errors: List<BudgetFormError>) : SaveBudgetResult
    data class Failed(val error: DomainError) : SaveBudgetResult
}
