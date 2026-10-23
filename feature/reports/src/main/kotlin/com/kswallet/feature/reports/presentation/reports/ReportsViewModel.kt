package com.kswallet.feature.reports.presentation.reports

import androidx.lifecycle.viewModelScope
import com.kswallet.core.common.time.TimeProvider
import com.kswallet.core.ui.screen.ScreenViewModel
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.feature.reports.R
import com.kswallet.feature.reports.domain.model.MonthlyReport
import com.kswallet.feature.reports.domain.model.ReportMonth
import com.kswallet.feature.reports.domain.usecase.ObserveBaseCurrencyUseCase
import com.kswallet.feature.reports.domain.usecase.ObserveMonthlyReportUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.KoinViewModel

/**
 * The reports screen.
 *
 * Three inputs decide what is shown - the selected month, the currency the settings give, and
 * a retry counter - and everything else is a projection of one database stream, so a
 * transaction edited anywhere in the app moves this screen without it asking. The selected
 * month is a `StateFlow` rather than a field of the state because *it* is an input to the
 * query, not a result of it.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@KoinViewModel
class ReportsViewModel(
    timeProvider: TimeProvider,
    observeBaseCurrency: ObserveBaseCurrencyUseCase,
    observeMonthlyReport: ObserveMonthlyReportUseCase,
) : ScreenViewModel<ReportsState, ReportsAction, ReportsEvent>(
    ReportsState(month = ReportMonth.of(timeProvider.today())),
) {

    /** The current month is the upper bound: a report about the future is not a report. */
    private val currentMonth: ReportMonth = ReportMonth.of(timeProvider.today())

    private val selectedMonth = MutableStateFlow(currentMonth)

    /** Bumped by `RetryClicked`; re-subscribes the database stream below. */
    private val retries = MutableStateFlow(0)

    init {
        combine(selectedMonth, observeBaseCurrency(), retries) { month, currency, retry ->
            Triple(month, currency, retry)
        }
            .onEach { (month, _, _) ->
                setState {
                    copy(
                        month = month,
                        canGoNext = month != currentMonth,
                        isLoading = true,
                        hasFailed = false,
                    )
                }
            }
            .flatMapLatest { (month, currency, _) ->
                observeMonthlyReport(month, currency)
                    // The catch belongs *inside* the switch, not at the end of the chain: a
                    // `catch` on the outer flow would complete it, and the retry below could
                    // never start a new subscription. Here a failed read ends only its own
                    // attempt, and the next tick re-subscribes.
                    .catch {
                        // A report has no writer, so the only failure it can hit is a broken read.
                        setState { copy(isLoading = false, hasFailed = true) }
                        sendEvent(ReportsEvent.ShowMessage(UiMessage.Resource(R.string.reports_error_load)))
                    }
            }
            .onEach { report -> setState { toUiStateFrom(report) } }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: ReportsAction) {
        when (action) {
            ReportsAction.PreviousMonthClicked -> selectedMonth.update { it.previous() }

            ReportsAction.NextMonthClicked -> selectedMonth.update { month ->
                if (month == currentMonth) month else month.next()
            }

            ReportsAction.RetryClicked -> retries.update { it + 1 }
        }
    }

    /** The one place a [MonthlyReport] becomes screen state. */
    private fun ReportsState.toUiStateFrom(report: MonthlyReport): ReportsState = copy(
        isLoading = false,
        hasFailed = false,
        currency = report.currency,
        income = report.income,
        expense = report.expense,
        net = report.net,
        averagePerDay = report.averagePerDay,
        daysElapsed = report.daysElapsed,
        isCurrentMonth = report.isCurrentMonth,
        changeVsPreviousMonth = report.changeVsPreviousMonth,
        categories = report.categories.map { row ->
            CategoryShareUi(
                categoryId = row.categoryId?.value,
                name = row.name,
                iconKey = row.iconKey,
                colorArgb = row.colorArgb,
                amount = row.amount,
                share = row.share,
                transactionCount = row.transactionCount,
            )
        },
        dailyExpenses = report.dailyExpenses,
    )
}
