package com.kswallet.feature.reports.domain.repository

import com.kswallet.core.model.Currency
import com.kswallet.feature.reports.domain.model.MonthlyReport
import com.kswallet.feature.reports.domain.model.ReportMonth
import kotlinx.coroutines.flow.Flow

/**
 * Read-only view of the ledger, aggregated for a report.
 *
 * Read-only is the point: a report has no writer, so it needs no `Outcome`-returning
 * commands - every failure a report can hit (a broken database) arrives as an exception on
 * the flow, which the ViewModel turns into an error state with a retry.
 */
interface ReportRepository {

    fun observeMonthlyReport(month: ReportMonth, currency: Currency): Flow<MonthlyReport>
}
