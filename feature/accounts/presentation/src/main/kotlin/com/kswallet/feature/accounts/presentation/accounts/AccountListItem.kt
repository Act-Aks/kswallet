package com.kswallet.feature.accounts.presentation.accounts

data class AccountListItem(
    val id: String,
    val name: String,
    val currencyCode: String,
    val isArchived: Boolean,
)