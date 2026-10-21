package com.kswallet.feature.accounts.presentation.list

import com.kswallet.core.model.AccountId

data class PendingAccountDelete(
    val id: AccountId,
    val name: String,
    val transactionCount: Int,
)
