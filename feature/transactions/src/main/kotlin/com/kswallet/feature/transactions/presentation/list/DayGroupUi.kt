package com.kswallet.feature.transactions.presentation.list

import com.kswallet.core.model.Money

/** One date section of the ledger, e.g. "Today", with its own subtotal. */
data class DayGroupUi(
    val header: String,
    val dayTotal: Money,
    val items: List<TransactionItemUi>,
)
