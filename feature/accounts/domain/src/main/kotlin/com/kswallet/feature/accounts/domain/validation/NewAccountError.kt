package com.kswallet.feature.accounts.domain.validation

import com.kswallet.core.money.CurrencyCode
import com.kswallet.feature.accounts.domain.model.AccountNameError

sealed interface NewAccountError {

    data class InvalidName(
        val reason: AccountNameError,
    ) : NewAccountError

    data object CurrencyRequired : NewAccountError

    data class UnsupportedCurrency(
        val code: CurrencyCode,
    ) : NewAccountError
}