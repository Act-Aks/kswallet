package com.kswallet.feature.transactions.domain.validation

import com.kswallet.core.model.AccountId
import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.TransactionType

data class TransactionFormInput(
    val type: TransactionType,
    val amountText: String,
    val accountId: AccountId?,
    val transferAccountId: AccountId?,
    val categoryId: CategoryId?,
    val note: String,
)
