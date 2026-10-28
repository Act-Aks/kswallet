package com.kswallet.feature.accounts.domain.model

import com.kswallet.core.money.CurrencyUnit

data class NewAccount(
    val name: AccountName,
    val currency: CurrencyUnit,
)