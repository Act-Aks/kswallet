package com.kswallet.core.database.model

import androidx.room3.ColumnInfo

/** Aggregated cash flow of a period, in one currency. */
data class CashFlowRow(
    @ColumnInfo(name = "income_minor")
    val incomeMinor: Long,

    @ColumnInfo(name = "expense_minor")
    val expenseMinor: Long,
)
