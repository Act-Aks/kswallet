package com.kswallet.feature.home.domain.model

import com.kswallet.core.model.Money
import com.kswallet.feature.transactions.domain.model.TransactionDetails

/**
 * Everything the dashboard shows, in one object.
 *
 * The screen renders this and nothing else, so "what does home show?" is answered by this
 * type instead of by three ViewModels.
 */
data class HomeSummary(
    val totalBalance: Money,
    val accountCount: Int,
    val monthIncome: Money,
    val monthExpense: Money,
    val recentTransactions: List<TransactionDetails>,
) {
    val hasAccounts: Boolean get() = accountCount > 0
    val hasRecentActivity: Boolean get() = recentTransactions.isNotEmpty()
}
