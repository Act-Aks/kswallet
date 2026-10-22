package com.kswallet.feature.home.presentation.home

import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import com.kswallet.core.ui.screen.ScreenState

data class HomeState(
    val isLoading: Boolean = true,
    val currency: Currency = Currency.Default,
    val totalBalance: Money? = null,
    val monthIncome: Money? = null,
    val monthExpense: Money? = null,
    val netThisMonth: Money? = null,
    val accountCount: Int = 0,
    val recentTransactions: List<RecentTransactionUi> = emptyList(),
) : ScreenState {
    /** A brand-new install: the dashboard offers to create the first account. */
    val needsFirstAccount: Boolean get() = !isLoading && accountCount == 0
}
