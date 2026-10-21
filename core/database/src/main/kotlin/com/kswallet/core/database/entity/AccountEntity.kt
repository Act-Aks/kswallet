package com.kswallet.core.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

/**
 * Persistence model of a wallet.
 *
 * Note what is *not* here: no `balance` column. A balance is always the initial balance
 * plus the effect of the transactions, and computing it in SQL (see `AccountDao`) makes it
 * impossible for the ledger and the balance to disagree.
 */
@Entity(
    tableName = "accounts",
    indices = [Index("is_archived"), Index("currency_code")],
)
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "currency_code")
    val currencyCode: String,

    @ColumnInfo(name = "initial_balance_minor")
    val initialBalanceMinor: Long,

    @ColumnInfo(name = "icon_key")
    val iconKey: String,

    @ColumnInfo(name = "color_argb")
    val colorArgb: Long,

    @ColumnInfo(name = "included_in_total")
    val includedInTotal: Boolean = true,

    @ColumnInfo(name = "is_archived")
    val isArchived: Boolean = false,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
)
