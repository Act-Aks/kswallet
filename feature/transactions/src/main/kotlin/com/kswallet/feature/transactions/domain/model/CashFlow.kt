package com.kswallet.feature.transactions.domain.model

import com.kswallet.core.model.Money

/** Income/expense totals of a period. */
data class CashFlow(
    val income: Money,
    val expense: Money,
) {
    val net: Money get() = income - expense
}
