package com.kswallet.feature.budgets.data.mapper

import com.kswallet.core.database.entity.BudgetEntity
import com.kswallet.core.database.model.BudgetWithCategoryRow
import com.kswallet.core.model.BudgetId
import com.kswallet.core.model.Category
import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.CategoryType
import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import com.kswallet.feature.budgets.domain.model.Budget
import com.kswallet.feature.budgets.domain.model.BudgetPeriod
import com.kswallet.feature.budgets.domain.model.BudgetProgress
import kotlin.time.Instant

/**
 * Entity <-> domain, in one place.
 *
 * Facts learned in the database stay here: the period is stored as a string, timestamps as
 * epoch milliseconds, money as minor units plus a currency code. Everything above this file
 * works with [Money], [BudgetPeriod] and [Instant].
 *
 * An unknown currency code (a row written by a newer version of the app) falls back to the
 * default instead of crashing: a slightly wrong symbol beats a screen that will not open.
 */
object BudgetMapper {

    fun toDomain(entity: BudgetEntity): Budget = Budget(
        id = BudgetId(entity.id),
        categoryId = CategoryId(entity.categoryId),
        limit = Money(entity.limitMinor, Currency.fromCode(entity.currencyCode) ?: Currency.Default),
        period = entity.period.toBudgetPeriod(),
        createdAt = Instant.fromEpochMilliseconds(entity.createdAt),
    )

    fun toEntity(budget: Budget, now: Instant): BudgetEntity = BudgetEntity(
        id = budget.id.value,
        categoryId = budget.categoryId.value,
        limitMinor = budget.limit.minorUnits,
        currencyCode = budget.limit.currency.code,
        period = budget.period.name,
        createdAt = (budget.createdAt ?: now).toEpochMilliseconds(),
    )

    /** The list row: a budget, its category and what has been spent in the current window. */
    fun toProgress(row: BudgetWithCategoryRow, spent: Money): BudgetProgress = BudgetProgress(
        budget = toDomain(row.budget),
        category = Category(
            id = CategoryId(row.budget.categoryId),
            name = row.categoryName,
            type = CategoryType.EXPENSE,
            iconKey = row.categoryIconKey,
            colorArgb = row.categoryColorArgb,
            createdAt = Instant.fromEpochMilliseconds(0),
        ),
        spent = spent,
    )

    /**
     * A period name written by a newer version of the app is read as `MONTHLY` rather than
     * throwing: the budget then shows a month's spending, which is the least surprising
     * interpretation, and the user can fix it by editing the budget.
     */
    private fun String.toBudgetPeriod(): BudgetPeriod =
        BudgetPeriod.entries.firstOrNull { it.name == this } ?: BudgetPeriod.MONTHLY
}
