package com.kswallet.core.money

import java.math.BigDecimal
import java.math.RoundingMode

object MoneyParser {

    /**
     * Maximum raw input length, including surrounding whitespace.
     *
     * This is an input-processing limit, not a monetary amount limit.
     */
    const val MAX_INPUT_LENGTH: Int = 256

    private val canonicalPattern =
        Regex("""[+-]?[0-9]+(?:\.[0-9]+)?""")

    fun parseCanonical(
        input: String,
        currency: CurrencyUnit,
    ): MoneyParseResult {
        if (input.length > MAX_INPUT_LENGTH) {
            return MoneyParseResult.Failure(
                MoneyParseError.InputTooLong,
            )
        }

        val normalized = input.trim()

        if (normalized.isEmpty()) {
            return MoneyParseResult.Failure(
                MoneyParseError.Empty,
            )
        }

        if (!canonicalPattern.matches(normalized)) {
            return MoneyParseResult.Failure(
                MoneyParseError.InvalidFormat,
            )
        }

        val decimal = try {
            BigDecimal(normalized)
        } catch (_: NumberFormatException) {
            return MoneyParseResult.Failure(
                MoneyParseError.InvalidFormat,
            )
        }

        val scaled = try {
            decimal.setScale(
                currency.minorUnitScale,
                RoundingMode.UNNECESSARY,
            )
        } catch (_: ArithmeticException) {
            return MoneyParseResult.Failure(
                MoneyParseError.PrecisionLoss,
            )
        }

        val minorUnits = try {
            scaled.unscaledValue().longValueExact()
        } catch (_: ArithmeticException) {
            return MoneyParseResult.Failure(
                MoneyParseError.OutOfRange,
            )
        }

        return MoneyParseResult.Success(
            Money(
                minorUnits = minorUnits,
                currency = currency,
            ),
        )
    }
}