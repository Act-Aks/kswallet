package com.kswallet.core.database.model

import androidx.room3.ColumnInfo

/**
 * Income and expenses that happened at one instant.
 *
 * The SQL deliberately groups by the *instant* and not by a calendar day: which day an
 * instant belongs to depends on the user's time zone, and the time zone lives in
 * `TimeProvider`, not in SQLite (the database would otherwise disagree with the ledger about
 * which day a late-evening transaction belongs to). The repository rolls these points into
 * local days.
 */
data class CashFlowPointRow(
    @ColumnInfo(name = "occurred_at")
    val occurredAt: Long,

    @ColumnInfo(name = "income_minor")
    val incomeMinor: Long,

    @ColumnInfo(name = "expense_minor")
    val expenseMinor: Long,
)
