package com.kswallet.feature.transactions.domain.model

import com.kswallet.core.model.Transaction

/**
 * A ledger entry with everything a list item shows.
 *
 * The join happens in SQL (one query per screen, not N+1), and this type is what the
 * presentation layer renders - it never touches an entity.
 */
data class TransactionDetails(
    val transaction: Transaction,
    val accountName: String,
    val transferAccountName: String?,
    val categoryName: String?,
    val categoryIconKey: String?,
    val categoryColorArgb: Long?,
) {
    val id get() = transaction.id
    val type get() = transaction.type
    val amount get() = transaction.amount
    val dateTime get() = transaction.dateTime
    val note get() = transaction.note
}
