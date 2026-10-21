package com.kswallet.feature.accounts.presentation.list

import com.kswallet.core.model.AccountId
import com.kswallet.core.model.AccountType
import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money

data class AccountCardUi(
    val id: AccountId,
    val name: String,
    val type: AccountType,
    val currency: Currency,
    val balance: Money,
    val colorArgb: Long,
    val includedInTotal: Boolean,
)
