package com.kswallet.feature.budgets.domain.validation

import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import com.kswallet.feature.budgets.domain.model.BudgetPeriod

data class BudgetFormInput(
    val categoryId: CategoryId?,
    val limitText: String,
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
)

sealed interface BudgetFormError {
    data object CategoryMissing : BudgetFormError
    data object InvalidLimit : BudgetFormError
    data object LimitNotPositive : BudgetFormError
}

/**
 * The rules of a budget form, as a pure object - no Compose, no ViewModel, no storage, so
 * they are unit-tested directly and reused by the editor as it is.
 */
object BudgetFormValidator {

    fun validate(input: BudgetFormInput, currency: Currency): List<BudgetFormError> = buildList {
        if (input.categoryId == null) add(BudgetFormError.CategoryMissing)

        val limit = Money.parseOrNull(input.limitText, currency)
        when {
            input.limitText.isBlank() || limit == null -> add(BudgetFormError.InvalidLimit)
            !limit.isPositive -> add(BudgetFormError.LimitNotPositive)
        }
    }
}
