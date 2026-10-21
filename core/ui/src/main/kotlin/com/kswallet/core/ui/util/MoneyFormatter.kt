package com.kswallet.core.ui.util

import com.kswallet.core.model.Money
import java.text.NumberFormat
import java.util.Locale
import java.util.Currency as JavaCurrency

/**
 * Formats money for display. The only place in the app that knows about
 * `NumberFormat` - domain and data layers only ever see [Money].
 */
object MoneyFormatter {

    /** `₹1,234.50` */
    fun format(
        money: Money,
        locale: Locale = Locale.getDefault(),
        includeSymbol: Boolean = true,
    ): String {
        val formatter = NumberFormat.getCurrencyInstance(locale).apply {
            this.currency = JavaCurrency.getInstance(money.currency.code)
            minimumFractionDigits = money.currency.decimalDigits
            maximumFractionDigits = money.currency.decimalDigits
        }
        val text = formatter.format(money.toMajorUnits())
        return if (includeSymbol) text else text.filter { it.isDigit() || it == '.' || it == ',' || it == '-' || it == '\u00A0' }
    }

    /** `+₹1,200.00` / `-₹300.00` - the sign is what makes the colour redundant, not the other way round. */
    fun formatSigned(money: Money, locale: Locale = Locale.getDefault()): String {
        val sign = if (money.isNegative) "-" else "+"
        return sign + format(money.abs(), locale)
    }

    /**
     * Compact form for dashboard chips: `₹12.3K`.
     *
     * Written by hand instead of with `NumberFormat.getCompactNumberInstance`, which is not
     * part of android.jar - and a hand-rolled suffix keeps the symbol where a wallet wants
     * it (in front of the number) on every locale, not where the JDK would put it.
     */
    fun formatCompact(money: Money, locale: Locale = Locale.getDefault()): String {
        val value = money.toMajorUnits().toDouble()
        val magnitude = kotlin.math.abs(value)
        val (scaled, suffix) = when {
            magnitude >= 1_000_000_000 -> value / 1_000_000_000 to "B"
            magnitude >= 1_000_000 -> value / 1_000_000 to "M"
            magnitude >= 1_000 -> value / 1_000 to "K"
            else -> value to ""
        }
        val number = if (suffix.isEmpty()) {
            String.format(locale, "%.0f", scaled)
        } else {
            String.format(locale, "%.1f", scaled)
        }
        return money.currency.symbol + number + suffix
    }

    /**
     * Keeps only what a numeric field should accept, so a paste of "₹1,234.50" becomes
     * "1234.50" instead of an error message.
     */
    fun sanitizeInput(input: String, decimalDigits: Int = 2): String {
        val digitsOnly = input.filter { it.isDigit() || it == '.' || it == ',' || it == '-' }
        val normalized = digitsOnly.replace(",", "")
        val negative = normalized.startsWith("-")
        val withoutSign = normalized.removePrefix("-")
        val parts = withoutSign.split('.')
        val whole = parts.firstOrNull().orEmpty().ifEmpty { "0" }
        val fraction = parts.getOrNull(1)?.take(decimalDigits)
        val rebuilt = if (fraction == null) whole else "$whole.$fraction"
        return if (negative) "-$rebuilt" else rebuilt
    }
}
