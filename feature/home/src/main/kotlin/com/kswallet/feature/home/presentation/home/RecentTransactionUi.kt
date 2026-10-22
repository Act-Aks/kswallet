package com.kswallet.feature.home.presentation.home

import com.kswallet.core.model.Money
import com.kswallet.core.model.TransactionType

data class RecentTransactionUi(
    val id: Long,
    val title: String,
    val subtitle: String,
    val amount: Money,
    val type: TransactionType,
    val iconKey: String?,
    val colorArgb: Long?,
)
