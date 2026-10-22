package com.kswallet.feature.budgets.domain.validation

sealed interface BudgetFormError {
    data object CategoryMissing : BudgetFormError
    data object InvalidLimit : BudgetFormError
    data object LimitNotPositive : BudgetFormError
}
