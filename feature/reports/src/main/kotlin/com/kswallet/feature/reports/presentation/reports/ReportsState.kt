package com.kswallet.feature.reports.presentation.reports

import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import com.kswallet.core.ui.screen.ScreenState
import com.kswallet.feature.reports.domain.model.ReportMonth

/**
 * One month of the ledger, ready to render.
 *
 * Money stays [Money] (the screen formats it) and the month stays a [ReportMonth] (the screen
 * formats its name in the user's language) - the state carries facts, not strings.
 */
data class ReportsState(
    val month: ReportMonth,
    val isLoading: Boolean = true,
    val canGoNext: Boolean = false,
    val currency: Currency = Currency.Default,
    val income: Money? = null,
    val expense: Money? = null,
    val net: Money? = null,
    val averagePerDay: Money? = null,
    val daysElapsed: Int = 0,
    val isCurrentMonth: Boolean = false,
    val changeVsPreviousMonth: Float? = null,
    val categories: List<CategoryShareUi> = emptyList(),
    val dailyExpenses: List<Money> = emptyList(),
    val hasFailed: Boolean = false,
) : ScreenState {

    /** Nothing earned and nothing spent: the screen says so instead of drawing zeros. */
    val isEmpty: Boolean
        get() = !isLoading && !hasFailed && (income?.isZero != false) && (expense?.isZero != false)

    val hasComparison: Boolean get() = changeVsPreviousMonth != null
}
