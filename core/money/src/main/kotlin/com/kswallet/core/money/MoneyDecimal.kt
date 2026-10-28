package com.kswallet.core.money

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Returns the exact decimal amount expressed in major units.
 */
fun Money.toMajorUnits(): BigDecimal =
    BigDecimal.valueOf(
        minorUnits,
        currency.minorUnitScale,
    )

/**
 * Returns a non-localized decimal representation with the currency's scale.
 *
 * Not intended as a user-facing formatter.
 */
fun Money.toCanonicalString(): String =
    toMajorUnits().toPlainString()

/**
 * Converts a decimal amount to exact stored minor units.
 *
 * By default, any required rounding fails with ArithmeticException.
 * Overflow also fails with ArithmeticException.
 */
fun Money.Companion.fromMajorUnits(
    amount: BigDecimal,
    currency: CurrencyUnit,
    roundingMode: RoundingMode = RoundingMode.UNNECESSARY,
): Money {
    val scaled = amount.setScale(
        currency.minorUnitScale,
        roundingMode,
    )

    return Money(
        minorUnits = scaled
            .unscaledValue()
            .longValueExact(),
        currency = currency,
    )
}