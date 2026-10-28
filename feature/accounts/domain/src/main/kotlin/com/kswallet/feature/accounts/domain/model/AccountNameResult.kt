package com.kswallet.feature.accounts.domain.model

sealed interface AccountNameResult {

    data class Valid(
        val name: AccountName,
    ) : AccountNameResult

    data class Invalid(
        val reason: AccountNameError,
    ) : AccountNameResult
}