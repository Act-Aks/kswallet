package com.kswallet.feature.budgets.domain.usecase

import com.kswallet.core.common.result.Outcome
import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import com.kswallet.feature.budgets.domain.model.Budget
import com.kswallet.feature.budgets.domain.repository.BudgetRepository
import com.kswallet.feature.budgets.domain.validation.BudgetFormInput
import com.kswallet.feature.budgets.domain.validation.BudgetFormValidator

class SaveBudgetUseCase(
    private val repository: BudgetRepository,
) {
    /**
     * Validates the form and writes the budget.
     *
     * The validator has already proven the form parses, so this only assembles the entity: a
     * new budget starts from the input, an existing one keeps its id and creation time and
     * takes the edited limit and period (the category is fixed once the budget exists, which
     * is also what the unique index per category and period enforces).
     */
    suspend operator fun invoke(
        existing: Budget?,
        input: BudgetFormInput,
        currency: Currency,
    ): SaveBudgetResult {
        val errors = BudgetFormValidator.validate(input, currency)
        if (errors.isNotEmpty()) return SaveBudgetResult.Invalid(errors)

        val categoryId = requireNotNull(input.categoryId)
        val limit = requireNotNull(Money.parseOrNull(input.limitText, currency))
        val budget = (existing ?: Budget(categoryId = categoryId, limit = limit)).copy(
            categoryId = categoryId,
            limit = limit,
            period = input.period,
        )

        return when (val result = repository.upsertBudget(budget)) {
            is Outcome.Success -> SaveBudgetResult.Saved(result.value)
            is Outcome.Failure -> SaveBudgetResult.Failed(result.error)
        }
    }
}
