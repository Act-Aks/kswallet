package com.kswallet.feature.reports.domain.model

import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money

/**
 * A month of the ledger, summarized.
 *
 * Every number here is derived from transactions - nothing is stored - so a report can never
 * disagree with the ledger it reports on. The screen renders this object and computes nothing
 * itself, which is what lets the interesting rules (what counts as spending, how a share is
 * rounded, what "average per day" means mid-month) be unit-tested without a device.
 */
data class MonthlyReport(
    val month: ReportMonth,
    val currency: Currency,
    val income: Money,
    val expense: Money,
    val previousMonthExpense: Money,
    val categories: List<CategoryReportRow>,
    val dailyExpenses: List<Money>,
    val daysElapsed: Int,
) {
    val net: Money get() = income - expense

    /** True while the month is still running: below-average spending is not "good" yet. */
    val isCurrentMonth: Boolean get() = daysElapsed < month.daysInMonth

    val hasActivity: Boolean get() = !income.isZero || !expense.isZero

    /** The biggest single category - what a report should lead with. */
    val topCategory: CategoryReportRow? get() = categories.maxByOrNull { it.amount }

    /**
     * Spending of this month compared to the previous one, as a fraction (`0.25` = a quarter
     * more). `null` when the previous month had no spending at all: "infinitely more" is not
     * a number a screen should print.
     */
    val changeVsPreviousMonth: Float?
        get() = if (previousMonthExpense.isZero) {
            null
        } else {
            expense.minorUnits.toFloat() / previousMonthExpense.minorUnits.toFloat() - 1f
        }

    /**
     * Spending divided by the days that have actually happened, floored to the currency's
     * minor unit. Dividing a month that is still running by its full length would flatter the
     * number all month long.
     */
    val averagePerDay: Money
        get() = if (daysElapsed <= 0 || expense.isZero) {
            Money.zero(currency)
        } else {
            Money(expense.minorUnits / daysElapsed, currency)
        }
}
