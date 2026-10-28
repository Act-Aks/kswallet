package com.kswallet.core.money

class CurrencyMismatchException(
    val leftCurrency: CurrencyUnit,
    val rightCurrency: CurrencyUnit,
) : IllegalArgumentException(
    "Currency definitions must match: " +
            leftCurrency.code.value +
            "(scale=${leftCurrency.minorUnitScale}) vs " +
            rightCurrency.code.value +
            "(scale=${rightCurrency.minorUnitScale}).",
)