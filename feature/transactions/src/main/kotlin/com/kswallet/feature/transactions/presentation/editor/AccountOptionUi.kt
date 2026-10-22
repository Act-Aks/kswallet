package com.kswallet.feature.transactions.presentation.editor

import com.kswallet.core.model.AccountId
import com.kswallet.core.model.Currency

data class AccountOptionUi(
    val id: AccountId,
    val name: String,
    val currency: Currency,
)
