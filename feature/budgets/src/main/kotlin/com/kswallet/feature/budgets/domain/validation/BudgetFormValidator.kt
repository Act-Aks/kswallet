package com.kswallet.feature.budgets.domain.validation

import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money

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
