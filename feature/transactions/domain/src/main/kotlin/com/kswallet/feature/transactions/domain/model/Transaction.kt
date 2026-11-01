package com.kswallet.feature.transactions.domain.model

import com.kswallet.core.identity.AccountId
import com.kswallet.core.identity.TransactionId
import com.kswallet.core.money.Money

data class Transaction(
    val id: TransactionId,
    val accountId: AccountId,
    val type: TransactionType,
    val amount: Money,
    val timestamp: TransactionTimestamp,
    val note: TransactionNote? = null,
) {

    init {
        require(amount.isPositive) {
            "Transaction amount must be positive."
        }
    }

    val signedAmount: Money
        get() = when (type) {
            TransactionType.Income -> amount

            TransactionType.Expense -> -amount
        }
}