package com.kswallet.feature.transactions.domain.model

import com.kswallet.core.identity.AccountId
import com.kswallet.core.money.Money

data class NewTransaction(
    val accountId: AccountId,
    val type: TransactionType,
    val amount: Money,
    val timestamp: TransactionTimestamp,
    val note: TransactionNote? = null,
)