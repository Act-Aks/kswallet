package com.kswallet.feature.accounts.domain.validation

import com.kswallet.core.model.AccountType
import com.kswallet.core.model.Currency

data class AccountFormInput(
    val name: String,
    val type: AccountType,
    val currency: Currency,
    val initialBalanceText: String,
    val colorArgb: Long,
    val includedInTotal: Boolean,
)
