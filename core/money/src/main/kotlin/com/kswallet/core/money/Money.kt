package com.kswallet.core.money

data class Money(
    val minorUnits: Long,
    val currency: CurrencyUnit,
) {

    val isZero: Boolean
        get() = minorUnits == 0L

    val isPositive: Boolean
        get() = minorUnits > 0L

    val isNegative: Boolean
        get() = minorUnits < 0L

    operator fun plus(other: Money): Money {
        requireSameCurrency(other)

        return copy(
            minorUnits = Math.addExact(
                minorUnits,
                other.minorUnits,
            ),
        )
    }

    operator fun minus(other: Money): Money {
        requireSameCurrency(other)

        return copy(
            minorUnits = Math.subtractExact(
                minorUnits,
                other.minorUnits,
            ),
        )
    }

    operator fun unaryMinus(): Money =
        copy(
            minorUnits = Math.negateExact(minorUnits),
        )

    operator fun compareTo(other: Money): Int {
        requireSameCurrency(other)

        return minorUnits.compareTo(other.minorUnits)
    }

    private fun requireSameCurrency(other: Money) {
        if (currency != other.currency) {
            throw CurrencyMismatchException(
                leftCurrency = currency,
                rightCurrency = other.currency,
            )
        }
    }

    companion object {

        fun zero(currency: CurrencyUnit): Money =
            Money(
                minorUnits = 0L,
                currency = currency,
            )
    }
}