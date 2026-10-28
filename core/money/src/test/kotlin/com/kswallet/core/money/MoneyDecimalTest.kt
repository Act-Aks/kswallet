package com.kswallet.core.money

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.math.BigDecimal
import java.math.RoundingMode

class MoneyDecimalTest {

    private fun currency(scale: Int): CurrencyUnit =
        CurrencyUnit(
            code = CurrencyCode.of("ABC"),
            minorUnitScale = scale,
        )

    @ParameterizedTest
    @CsvSource(
        "0, 125, 125",
        "2, 12550, 125.50",
        "3, 12550, 12.550",
        "2, -12550, -125.50",
        "2, 0, 0.00",
    )
    fun `converts minor units to exact decimal`(
        scale: Int,
        minorUnits: Long,
        expected: String,
    ) {
        val money = Money(
            minorUnits = minorUnits,
            currency = currency(scale),
        )

        assertEquals(
            BigDecimal(expected),
            money.toMajorUnits(),
        )

        assertEquals(
            expected,
            money.toCanonicalString(),
        )
    }

    @ParameterizedTest
    @CsvSource(
        "0, 125, 125",
        "2, 125.50, 12550",
        "2, 125.5, 12550",
        "2, 125.500, 12550",
        "3, 12.345, 12345",
        "2, -0.01, -1",
    )
    fun `converts exact decimals into minor units`(
        scale: Int,
        input: String,
        expectedMinorUnits: Long,
    ) {
        val result = Money.fromMajorUnits(
            amount = BigDecimal(input),
            currency = currency(scale),
        )

        assertEquals(
            expectedMinorUnits,
            result.minorUnits,
        )
    }

    @Test
    fun `default conversion rejects required rounding`() {
        assertThrows(ArithmeticException::class.java) {
            Money.fromMajorUnits(
                amount = BigDecimal("1.001"),
                currency = currency(2),
            )
        }
    }

    @ParameterizedTest
    @CsvSource(
        "1.005, HALF_EVEN, 100",
        "1.015, HALF_EVEN, 102",
        "1.005, HALF_UP, 101",
        "-1.005, HALF_UP, -101",
        "1.009, DOWN, 100",
        "-1.009, DOWN, -100",
        "-1.001, FLOOR, -101",
    )
    fun `rounding follows the explicit policy`(
        input: String,
        roundingMode: RoundingMode,
        expectedMinorUnits: Long,
    ) {
        val result = Money.fromMajorUnits(
            amount = BigDecimal(input),
            currency = currency(2),
            roundingMode = roundingMode,
        )

        assertEquals(
            expectedMinorUnits,
            result.minorUnits,
        )
    }

    @Test
    fun `conversion rejects overflow`() {
        assertThrows(ArithmeticException::class.java) {
            Money.fromMajorUnits(
                amount = BigDecimal("92233720368547758.08"),
                currency = currency(2),
            )
        }
    }

    @Test
    fun `maximum and minimum values round trip exactly`() {
        val unit = currency(2)

        listOf(
            Long.MIN_VALUE,
            -1L,
            0L,
            1L,
            Long.MAX_VALUE,
        ).forEach { minorUnits ->
            val original = Money(
                minorUnits = minorUnits,
                currency = unit,
            )

            val restored = Money.fromMajorUnits(
                amount = original.toMajorUnits(),
                currency = unit,
            )

            assertEquals(original, restored)
        }
    }
}