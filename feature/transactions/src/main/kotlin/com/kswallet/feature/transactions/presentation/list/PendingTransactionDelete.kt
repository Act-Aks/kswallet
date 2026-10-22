package com.kswallet.feature.transactions.presentation.list

import com.kswallet.core.model.TransactionId

data class PendingTransactionDelete(
    val id: TransactionId,
    val title: String,
)
