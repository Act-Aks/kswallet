package com.kswallet.core.money

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class MoneyParserTest {

    private fun currency(scale: Int = 2): CurrencyUnit =
        CurrencyUnit(
            code = CurrencyCode.of("ABC"),
            minorUnitScale = scale,
        )

    @ParameterizedTest
    @CsvSource(
        "125, 12500",
        "125.50, 12550",
        "125.5, 12550",
        "125.500, 12550",
        "-125.50, -12550",
        "+125.50, 12550",
        "00012.50, 1250",
        "0.25, 25",
        "-0.00, 0",
    )
    fun `accepts exact canonical amounts`(
        input: String,
        expectedMinorUnits: Long,
    ) {
        val unit = currency()

        assertEquals(
            MoneyParseResult.Success(
                Money(
                    minorUnits = expectedMinorUnits,
                    currency = unit,
                ),
            ),
            MoneyParser.parseCanonical(input, unit),
        )
    }

    @Test
    fun `allows surrounding whitespace`() {
        val unit = currency()

        assertEquals(
            MoneyParseResult.Success(
                Money(12550, unit),
            ),
            MoneyParser.parseCanonical(
                input = " \t125.50\n",
                currency = unit,
            ),
        )
    }

    @Test
    fun `reports empty input`() {
        listOf("", " ", "\t\n").forEach { input ->
            assertFailure(
                input = input,
                expected = MoneyParseError.Empty,
            )
        }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "1,250.50",
            "125,50",
            "₹125.50",
            "1e3",
            ".50",
            "125.",
            "+",
            "-",
            "1 250",
            "NaN",
            "Infinity",
            "--1",
        ],
    )
    fun `rejects unsupported syntax`(input: String) {
        assertFailure(
            input = input,
            expected = MoneyParseError.InvalidFormat,
        )
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "1.001",
            "-1.001",
            "0.0001",
            "125.501",
        ],
    )
    fun `rejects value-changing precision loss`(
        input: String,
    ) {
        assertFailure(
            input = input,
            expected = MoneyParseError.PrecisionLoss,
        )
    }

    @Test
    fun `scale zero allows only exact whole values`() {
        val unit = currency(scale = 0)

        assertEquals(
            MoneyParseResult.Success(
                Money(125, unit),
            ),
            MoneyParser.parseCanonical("125.000", unit),
        )

        assertEquals(
            MoneyParseResult.Failure(
                MoneyParseError.PrecisionLoss,
            ),
            MoneyParser.parseCanonical("125.1", unit),
        )
    }

    @Test
    fun `supports three fractional digits when declared`() {
        val unit = currency(scale = 3)

        assertEquals(
            MoneyParseResult.Success(
                Money(12345, unit),
            ),
            MoneyParser.parseCanonical("12.345", unit),
        )
    }

    @Test
    fun `rejects excessively long raw input`() {
        assertFailure(
            input = "1".repeat(
                MoneyParser.MAX_INPUT_LENGTH + 1,
            ),
            expected = MoneyParseError.InputTooLong,
        )
    }

    @Test
    fun `accepts maximum long minor units`() {
        val unit = currency()

        assertEquals(
            MoneyParseResult.Success(
                Money(Long.MAX_VALUE, unit),
            ),
            MoneyParser.parseCanonical(
                "92233720368547758.07",
                unit,
            ),
        )
    }

    @Test
    fun `accepts minimum long minor units`() {
        val unit = currency()

        assertEquals(
            MoneyParseResult.Success(
                Money(Long.MIN_VALUE, unit),
            ),
            MoneyParser.parseCanonical(
                "-92233720368547758.08",
                unit,
            ),
        )
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "92233720368547758.08",
            "-92233720368547758.09",
        ],
    )
    fun `reports values outside long range`(
        input: String,
    ) {
        assertFailure(
            input = input,
            expected = MoneyParseError.OutOfRange,
        )
    }

    @Test
    fun `canonical representation parses back exactly`() {
        val unit = currency(scale = 3)

        listOf(
            Long.MIN_VALUE,
            -12345L,
            0L,
            12345L,
            Long.MAX_VALUE,
        ).forEach { minorUnits ->
            val original = Money(
                minorUnits = minorUnits,
                currency = unit,
            )

            assertEquals(
                MoneyParseResult.Success(original),
                MoneyParser.parseCanonical(
                    original.toCanonicalString(),
                    unit,
                ),
            )
        }
    }

    private fun assertFailure(
        input: String,
        expected: MoneyParseError,
    ) {
        assertEquals(
            MoneyParseResult.Failure(expected),
            MoneyParser.parseCanonical(
                input = input,
                currency = currency(),
            ),
        )
    }
}