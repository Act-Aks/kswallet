package com.kswallet.core.model

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * An amount of money in a single currency, stored in the currency's minor unit
 * (paise, cents, ...) so that no rounding error can ever creep into the ledger.
 *
 * Money is immutable; every operation returns a new instance and refuses to mix
 * currencies, which is what keeps balances and reports honest.
 */
data class Money(val minorUnits: Long, val currency: Currency) : Comparable<Money> {

    operator fun plus(other: Money): Money = Money(minorUnits + other.minorUnits, sameCurrency(other))

    operator fun minus(other: Money): Money = Money(minorUnits - other.minorUnits, sameCurrency(other))

    operator fun times(multiplier: Int): Money = Money(minorUnits * multiplier, currency)

    operator fun unaryMinus(): Money = Money(-minorUnits, currency)

    fun abs(): Money = if (isNegative) -this else this

    val isZero: Boolean get() = minorUnits == 0L
    val isPositive: Boolean get() = minorUnits > 0L
    val isNegative: Boolean get() = minorUnits < 0L

    /** Major units, e.g. `1234.50` for 123450 paise. For formatting and tests. */
    fun toMajorUnits(): BigDecimal = BigDecimal.valueOf(minorUnits, currency.decimalDigits)

    override fun compareTo(other: Money): Int {
        sameCurrency(other)
        return minorUnits.compareTo(other.minorUnits)
    }

    private fun sameCurrency(other: Money): Currency {
        require(currency == other.currency) {
            "Cannot combine ${currency.code} with ${other.currency.code}"
        }
        return currency
    }

    companion object {
        fun zero(currency: Currency): Money = Money(0L, currency)

        /** Builds Money from a major-unit value, rounding half-up to the currency's precision. */
        fun of(majorUnits: BigDecimal, currency: Currency): Money {
            val scaled = majorUnits.movePointRight(currency.decimalDigits).setScale(0, RoundingMode.HALF_UP)
            return Money(scaled.toLong(), currency)
        }

        fun of(majorUnits: Number, currency: Currency): Money = of(BigDecimal(majorUnits.toString()), currency)

        fun of(majorUnits: String, currency: Currency): Money = of(BigDecimal(majorUnits.trim()), currency)

        /** Returns null instead of throwing - used by the amount fields in the editor screens. */
        fun parseOrNull(input: String, currency: Currency): Money? =
            if (input.isBlank()) null else runCatching { of(input, currency) }.getOrNull()
    }
}

/**
 * Supported currencies. [decimalDigits] is the number of digits after the separator
 * used when converting between major and minor units (JPY has none).
 */
enum class Currency(
    val code: String,
    val symbol: String,
    val decimalDigits: Int,
) {
    INR("INR", "\u20B9", 2),
    USD("USD", "$", 2),
    EUR("EUR", "\u20AC", 2),
    GBP("GBP", "\u00A3", 2),
    AUD("AUD", "A$", 2),
    CAD("CAD", "C$", 2),
    CHF("CHF", "CHF", 2),
    AED("AED", "\u062F.\u0625", 2),
    SGD("SGD", "S$", 2),
    JPY("JPY", "\u00A5", 0),
    ;

    companion object {
        val Default: Currency = INR

        fun fromCode(code: String): Currency? = entries.firstOrNull { it.code.equals(code, ignoreCase = true) }
    }
}
