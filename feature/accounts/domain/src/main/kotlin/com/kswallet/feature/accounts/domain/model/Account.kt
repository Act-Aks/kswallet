package com.kswallet.feature.accounts.domain.model

import com.kswallet.core.money.CurrencyUnit

data class Account(
    val id: AccountId,
    val name: AccountName,
    val currency: CurrencyUnit,
    val status: AccountStatus,
) {
    val isArchived: Boolean
        get() = status == AccountStatus.Archived
}