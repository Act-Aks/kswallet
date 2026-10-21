package com.kswallet.core.database.model

import androidx.room3.ColumnInfo
import androidx.room3.Embedded
import com.kswallet.core.database.entity.AccountEntity

/** An account row plus its SQL-computed balance. */
data class AccountWithBalance(
    @Embedded
    val account: AccountEntity,

    @ColumnInfo(name = "balance_minor")
    val balanceMinor: Long,
)
