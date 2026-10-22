package com.kswallet.feature.transactions.presentation.list

import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import com.kswallet.core.ui.screen.ScreenState
import com.kswallet.feature.transactions.domain.model.TransactionFilter

data class TransactionsState(
    val isLoading: Boolean = true,
    val filter: TransactionFilter = TransactionFilter(),
    val currency: Currency = Currency.Default,
    val dayGroups: List<DayGroupUi> = emptyList(),
    val periodIncome: Money? = null,
    val periodExpense: Money? = null,
    val pendingDelete: PendingTransactionDelete? = null,
) : ScreenState {
    val isEmpty: Boolean get() = !isLoading && dayGroups.isEmpty()
}
