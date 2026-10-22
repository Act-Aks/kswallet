package com.kswallet.feature.budgets.domain.model

import com.kswallet.core.common.time.DateRange
import com.kswallet.core.common.time.TimeProvider
import com.kswallet.core.common.time.monthRangeOf
import com.kswallet.core.common.time.weekRangeOf
import com.kswallet.core.common.time.yearRangeOf
import com.kswallet.core.model.BudgetId
import com.kswallet.core.model.Category
import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.Money

/**
 * A spending limit for one category over one [BudgetPeriod].
 *
 * The limit is a [Money], not a number: "500 a month" is meaningless without the currency it
 * is counted in, and the ledger already refuses to mix currencies without a rate.
 */
data class Budget(
    val id: BudgetId = BudgetId.UNSAVED,
    val categoryId: CategoryId,
    val limit: Money,
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
    val createdAt: kotlin.time.Instant? = null,
)

enum class BudgetPeriod {
    WEEKLY,
    MONTHLY,
    YEARLY,
    ;

    /**
     * The window this period currently covers, as of [timeProvider]'s clock.
     *
     * It reuses the same range helpers as the ledger filters ("this month", "this year"), so a
     * monthly budget and the Transactions screen can never disagree about where a month ends.
     */
    fun currentRange(timeProvider: TimeProvider): DateRange = when (this) {
        WEEKLY -> timeProvider.weekRangeOf(timeProvider.today())
        MONTHLY -> timeProvider.monthRangeOf(timeProvider.today())
        YEARLY -> timeProvider.yearRangeOf(timeProvider.today())
    }
}

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
