package com.kswallet.core.model

import kotlin.time.Instant

/**
 * A ledger entry. Three shapes exist:
 *  - INCOME: money arrives on [accountId]
 *  - EXPENSE: money leaves [accountId]
 *  - TRANSFER: money moves from [accountId] to [transferAccountId]
 *
 * [categoryId] only applies to income/expense; transfers are categorised by definition.
 */
data class Transaction(
    val id: TransactionId,
    val type: TransactionType,
    val amount: Money,
    val accountId: AccountId,
    val transferAccountId: AccountId? = null,
    val categoryId: CategoryId? = null,
    val dateTime: Instant,
    val note: String? = null,
    val createdAt: Instant,
) {
    val isPersisted: Boolean get() = id.isSaved
}

enum class TransactionType {
    INCOME,
    EXPENSE,
    TRANSFER,
    ;

    val isTransfer: Boolean get() = this == TRANSFER
}
