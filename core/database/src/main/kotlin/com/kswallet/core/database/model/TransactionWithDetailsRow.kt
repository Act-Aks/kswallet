package com.kswallet.core.database.model

import androidx.room3.ColumnInfo
import androidx.room3.Embedded
import com.kswallet.core.database.entity.TransactionEntity

/** A ledger row joined with everything a list item needs to render. */
data class TransactionWithDetailsRow(
    @Embedded val transaction: TransactionEntity,
    @ColumnInfo(name = "account_name") val accountName: String,
    @ColumnInfo(name = "account_currency") val accountCurrencyCode: String,
    @ColumnInfo(name = "transfer_account_name") val transferAccountName: String?,
    @ColumnInfo(name = "category_name") val categoryName: String?,
    @ColumnInfo(name = "category_icon_key") val categoryIconKey: String?,
    @ColumnInfo(name = "category_color_argb") val categoryColorArgb: Long?,
)
