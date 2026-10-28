package com.kswallet.feature.accounts.domain.validation

import com.kswallet.feature.accounts.domain.model.NewAccount

sealed interface NewAccountValidation {

    data class Valid(
        val account: NewAccount,
    ) : NewAccountValidation

    data class Invalid(
        val reason: NewAccountError,
    ) : NewAccountValidation
}