package com.kswallet.feature.reports.data.repository

import com.kswallet.core.common.time.TimeProvider
import com.kswallet.core.common.time.monthRangeOf
import com.kswallet.core.database.dao.TransactionDao
import com.kswallet.core.model.Currency
import com.kswallet.feature.reports.data.mapper.ReportMapper
import com.kswallet.feature.reports.domain.model.MonthlyReport
import com.kswallet.feature.reports.domain.model.ReportMonth
import com.kswallet.feature.reports.domain.repository.ReportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus

/**
 * The report, assembled from four queries over the ledger.
 *
 * Nothing is cached and nothing is stored: every emission is a fresh projection of the
 * transactions, which is why a report can never be out of date. The four queries are the
 * month's cash flow, the *previous* month's cash flow (for "vs last month"), the spending per
 * category, and the raw points the chart needs - all filtered to accounts of the currency the
 * user thinks in, so that a mixed-currency ledger reports in one currency instead of adding
 * rupees to dollars.
 */
class OfflineFirstReportRepository(
    private val transactionDao: TransactionDao,
    private val timeProvider: TimeProvider,
) : ReportRepository {

    override fun observeMonthlyReport(month: ReportMonth, currency: Currency): Flow<MonthlyReport> {
        val current = timeProvider.monthRangeOf(month.startDate())
        val previous = timeProvider.monthRangeOf(month.startDate().minus(1, DateTimeUnit.MONTH))

        val from = current.start.toEpochMilliseconds()
        val to = current.endExclusive.toEpochMilliseconds()
        val previousFrom = previous.start.toEpochMilliseconds()
        val previousTo = previous.endExclusive.toEpochMilliseconds()

        // A month that is still running has only lived `today.day` days: dividing by its full
        // length would report a "daily average" the user has not actually spent. Past months
        // are complete, so they use their whole length.
        val daysElapsed = if (month == ReportMonth.of(timeProvider.today())) {
            timeProvider.today().day
        } else {
            month.daysInMonth
        }

        return combine(
            transactionDao.observeCashFlow(from, to, currency.code),
            transactionDao.observeCashFlow(previousFrom, previousTo, currency.code),
            transactionDao.observeCategorySpendWithDetails(from, to, currency.code),
            transactionDao.observeCashFlowPoints(from, to, currency.code),
        ) { cashFlow, previousMonth, categorySpend, points ->
            ReportMapper.toMonthlyReport(
                month = month,
                currency = currency,
                daysElapsed = daysElapsed,
                cashFlow = cashFlow,
                previousMonth = previousMonth,
                categorySpend = categorySpend,
                points = points,
                zone = timeProvider.timeZone(),
            )
        }
    }

    private fun ReportMonth.startDate(): LocalDate = LocalDate(year, month, 1)
}
