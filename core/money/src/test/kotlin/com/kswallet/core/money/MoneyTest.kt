package com.kswallet.core.money

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class MoneyTest {

    private val currency = CurrencyUnit(
        code = CurrencyCode.of("ABC"),
        minorUnitScale = 2,
    )

    private val otherCurrency = CurrencyUnit(
        code = CurrencyCode.of("XYZ"),
        minorUnitScale = 2,
    )

    private fun money(amount: Long): Money =
        Money(
            minorUnits = amount,
            currency = currency,
        )

    @Test
    fun `zero retains its currency`() {
        val zero = Money.zero(currency)

        assertEquals(0L, zero.minorUnits)
        assertEquals(currency, zero.currency)

        assertTrue(zero.isZero)
        assertFalse(zero.isPositive)
        assertFalse(zero.isNegative)
    }

    @Test
    fun `positive and negative signs are explicit`() {
        val positive = money(100)
        val negative = money(-100)

        assertTrue(positive.isPositive)
        assertFalse(positive.isNegative)

        assertTrue(negative.isNegative)
        assertFalse(negative.isPositive)
    }

    @ParameterizedTest
    @CsvSource(
        "100, 250, 350",
        "-100, 250, 150",
        "-100, -250, -350",
        "0, 250, 250",
    )
    fun `adds exact minor units`(
        left: Long,
        right: Long,
        expected: Long,
    ) {
        assertEquals(
            money(expected),
            money(left) + money(right),
        )
    }

    @ParameterizedTest
    @CsvSource(
        "500, 125, 375",
        "100, 250, -150",
        "-100, -250, 150",
        "0, 250, -250",
    )
    fun `subtracts exact minor units`(
        left: Long,
        right: Long,
        expected: Long,
    ) {
        assertEquals(
            money(expected),
            money(left) - money(right),
        )
    }

    @Test
    fun `negates an amount`() {
        assertEquals(money(-125), -money(125))
        assertEquals(money(125), -money(-125))
        assertEquals(money(0), -money(0))
    }

    @Test
    fun `compares matching currency definitions`() {
        assertTrue(money(100) < money(200))
        assertTrue(money(200) > money(100))
        assertEquals(0, money(100).compareTo(money(100)))
    }

    @Test
    fun `rejects addition across currency codes`() {
        val other = Money(
            minorUnits = 100,
            currency = otherCurrency,
        )

        assertThrows(CurrencyMismatchException::class.java) {
            money(100) + other
        }
    }

    @Test
    fun `rejects subtraction across currency codes`() {
        val other = Money(
            minorUnits = 100,
            currency = otherCurrency,
        )

        assertThrows(CurrencyMismatchException::class.java) {
            money(100) - other
        }
    }

    @Test
    fun `rejects ordering across currency codes`() {
        val other = Money(
            minorUnits = 100,
            currency = otherCurrency,
        )

        assertThrows(CurrencyMismatchException::class.java) {
            money(100).compareTo(other)
        }
    }

    @Test
    fun `rejects arithmetic when scales differ`() {
        val differentScale = Money(
            minorUnits = 100,
            currency = currency.copy(
                minorUnitScale = 3,
            ),
        )

        assertThrows(CurrencyMismatchException::class.java) {
            money(100) + differentScale
        }

        assertThrows(CurrencyMismatchException::class.java) {
            money(100) - differentScale
        }

        assertThrows(CurrencyMismatchException::class.java) {
            money(100).compareTo(differentScale)
        }
    }

    @Test
    fun `equality includes currency definition`() {
        assertEquals(money(100), money(100))
        assertNotEquals(money(100), money(101))

        assertNotEquals(
            money(100),
            Money(
                minorUnits = 100,
                currency = otherCurrency,
            ),
        )
    }

    @Test
    fun `addition fails on positive overflow`() {
        assertThrows(ArithmeticException::class.java) {
            money(Long.MAX_VALUE) + money(1)
        }
    }

    @Test
    fun `addition fails on negative overflow`() {
        assertThrows(ArithmeticException::class.java) {
            money(Long.MIN_VALUE) + money(-1)
        }
    }

    @Test
    fun `subtraction fails below minimum`() {
        assertThrows(ArithmeticException::class.java) {
            money(Long.MIN_VALUE) - money(1)
        }
    }

    @Test
    fun `subtraction fails above maximum`() {
        assertThrows(ArithmeticException::class.java) {
            money(Long.MAX_VALUE) - money(-1)
        }
    }

    @Test
    fun `negation fails for minimum long`() {
        assertThrows(ArithmeticException::class.java) {
            -money(Long.MIN_VALUE)
        }
    }

    @Test
    fun `boundary values remain valid without overflowing`() {
        assertEquals(
            money(Long.MAX_VALUE),
            money(Long.MAX_VALUE) + money(0),
        )

        assertEquals(
            money(Long.MIN_VALUE),
            money(Long.MIN_VALUE) - money(0),
        )
    }
}