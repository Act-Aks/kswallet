package com.kswallet.feature.accounts.data.error

import com.kswallet.core.money.CurrencyCode

class AccountCurrencyRejectedException(
    val code: CurrencyCode,
    val suppliedScale: Int,
    val registeredScale: Int?,
) : IllegalArgumentException(
    if (registeredScale == null) {
        "Currency ${code.value} is not supported for new accounts."
    } else {
        "Currency ${code.value} has scale $suppliedScale, " + "but the registry requires scale $registeredScale."
    },
)