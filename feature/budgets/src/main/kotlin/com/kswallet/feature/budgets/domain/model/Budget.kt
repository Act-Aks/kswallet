package com.kswallet.feature.budgets.domain.model

import com.kswallet.core.model.BudgetId
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
