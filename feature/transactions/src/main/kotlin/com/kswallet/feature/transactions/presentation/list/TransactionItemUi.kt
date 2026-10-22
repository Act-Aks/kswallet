package com.kswallet.feature.transactions.presentation.list

import com.kswallet.core.model.Money
import com.kswallet.core.model.TransactionId
import com.kswallet.core.model.TransactionType

data class TransactionItemUi(
    val id: TransactionId,
    val title: String,
    val subtitle: String,
    val amount: Money,
    val type: TransactionType,
    val iconKey: String?,
    val colorArgb: Long?,
)
