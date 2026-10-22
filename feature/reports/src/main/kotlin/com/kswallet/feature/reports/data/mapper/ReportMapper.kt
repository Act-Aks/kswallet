package com.kswallet.feature.reports.data.mapper

import com.kswallet.core.database.model.CashFlowPointRow
import com.kswallet.core.database.model.CashFlowRow
import com.kswallet.core.database.model.CategorySpendWithDetailsRow
import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import com.kswallet.feature.reports.domain.model.CategoryReportRow
import com.kswallet.feature.reports.domain.model.MonthlyReport
import com.kswallet.feature.reports.domain.model.ReportMonth
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/**
 * Database rows -> a report.
 *
 * Two decisions live here rather than in SQL:
 *  - **shares** are computed from the category rows themselves, so they add up to exactly 1
 *    whatever the totals say (a report whose percentages do not total 100% is a bug report);
 *  - **days** are derived from instants *in the app's time zone*, because the same instant is
 *    Tuesday in one zone and Wednesday in another and SQLite has no idea which one the user
 *    is in.
 */
object ReportMapper {

    /** The id the query gives to "no category" (`COALESCE(t.category_id, 0)`). */
    private const val UNCATEGORISED_ID = 0L

    fun toMonthlyReport(
        month: ReportMonth,
        currency: Currency,
        daysElapsed: Int,
        cashFlow: CashFlowRow,
        previousMonth: CashFlowRow,
        categorySpend: List<CategorySpendWithDetailsRow>,
        points: List<CashFlowPointRow>,
        zone: TimeZone,
    ): MonthlyReport = MonthlyReport(
        month = month,
        currency = currency,
        income = Money(cashFlow.incomeMinor, currency),
        expense = Money(cashFlow.expenseMinor, currency),
        previousMonthExpense = Money(previousMonth.expenseMinor, currency),
        categories = toCategoryRows(categorySpend, currency),
        dailyExpenses = toDailyExpenses(points, month, currency, zone),
        daysElapsed = daysElapsed,
    )

    private fun toCategoryRows(
        rows: List<CategorySpendWithDetailsRow>,
        currency: Currency,
    ): List<CategoryReportRow> {
        val total = rows.sumOf { it.spentMinor }
        return rows.map { row ->
            CategoryReportRow(
                categoryId = if (row.categoryId == UNCATEGORISED_ID) null else CategoryId(row.categoryId),
                name = row.categoryName,
                iconKey = row.categoryIconKey,
                colorArgb = row.categoryColorArgb,
                amount = Money(row.spentMinor, currency),
                transactionCount = row.transactionCount,
                share = if (total == 0L) 0f else row.spentMinor.toFloat() / total.toFloat(),
            )
        }
    }

    /**
     * One amount per day of the month, zeros included: the chart draws the month, not the days
     * that happen to have spending.
     */
    private fun toDailyExpenses(
        points: List<CashFlowPointRow>,
        month: ReportMonth,
        currency: Currency,
        zone: TimeZone,
    ): List<Money> {
        val perDay = points
            .groupBy { point -> point.occurredAt.toLocalDayOfMonth(zone) }
            .mapValues { (_, dayPoints) -> dayPoints.sumOf { it.expenseMinor } }

        return (1..month.daysInMonth).map { day -> Money(perDay[day] ?: 0L, currency) }
    }

    private fun Long.toLocalDayOfMonth(zone: TimeZone): Int =
        Instant.fromEpochMilliseconds(this).toLocalDateTime(zone).day
}
