package com.kswallet.feature.budgets.domain.model

import com.kswallet.core.model.Category
import com.kswallet.core.model.Money

/**
 * A budget with the category it limits and the part that has already been spent.
 *
 * [spent] is always in the budget's currency and comes from the ledger (`SUM` over the
 * category's expenses inside the period) - never from a stored counter, which is exactly the
 * kind of number that goes stale after an edit.
 */
data class BudgetProgress(
    val budget: Budget,
    val category: Category,
    val spent: Money,
) {
    val remaining: Money get() = budget.limit - spent

    val isOverBudget: Boolean get() = spent > budget.limit

    /** 0f..1f, clamped: an over-budget bar shows full, the [isOverBudget] flag carries the rest. */
    val fractionUsed: Float
        get() {
            val limit = budget.limit.minorUnits
            if (limit <= 0L) return if (spent.minorUnits > 0L) 1f else 0f
            val ratio = spent.minorUnits.toDouble() / limit.toDouble()
            return ratio.coerceIn(0.0, 1.0).toFloat()
        }
}
