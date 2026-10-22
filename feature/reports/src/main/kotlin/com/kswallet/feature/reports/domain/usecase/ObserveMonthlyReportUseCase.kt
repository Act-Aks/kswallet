package com.kswallet.feature.reports.domain.usecase

import com.kswallet.core.model.Currency
import com.kswallet.feature.reports.domain.model.MonthlyReport
import com.kswallet.feature.reports.domain.model.ReportMonth
import com.kswallet.feature.reports.domain.repository.ReportRepository
import kotlinx.coroutines.flow.Flow

/**
 * The report of one month.
 *
 * The month is a parameter rather than "now": the screen lets the user walk back and forth,
 * and a use case that reads the clock itself could not be asked for September.
 */
class ObserveMonthlyReportUseCase(
    private val repository: ReportRepository,
) {
    operator fun invoke(month: ReportMonth, currency: Currency): Flow<MonthlyReport> =
        repository.observeMonthlyReport(month, currency)
}
