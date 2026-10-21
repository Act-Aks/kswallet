package com.kswallet.core.model

import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MoneyTest {

    @Test
    fun `builds from major units using the currency precision`() {
        val amount = Money.of("1234.50", Currency.INR)

        assertEquals(123_450L, amount.minorUnits)
        assertEquals(BigDecimal("1234.50"), amount.toMajorUnits())
    }

    @Test
    fun `currencies without decimals keep their precision`() {
        assertEquals(1500L, Money.of("1500", Currency.JPY).minorUnits)
    }

    @Test
    fun `rounds half up when the input has too many decimals`() {
        assertEquals(1235L, Money.of("12.345", Currency.INR).minorUnits)
    }

    @Test
    fun `arithmetic stays exact`() {
        val a = Money.of("0.10", Currency.INR)
        val b = Money.of("0.20", Currency.INR)

        assertEquals(Money.of("0.30", Currency.INR), a + b)
        assertEquals(Money.of("-0.10", Currency.INR), a - b)
        assertEquals(Money.of("0.60", Currency.INR), a * 6)
    }

    @Test
    fun `mixing currencies fails fast`() {
        val rupees = Money.of("10.00", Currency.INR)
        val dollars = Money.of("10.00", Currency.USD)

        assertFailsWith<IllegalArgumentException> { rupees + dollars }
    }

    @Test
    fun `parseOrNull rejects garbage instead of crashing the editor`() {
        assertNull(Money.parseOrNull("", Currency.INR))
        assertNull(Money.parseOrNull("abc", Currency.INR))
        assertEquals(Money.of("12", Currency.INR), Money.parseOrNull("12", Currency.INR))
    }

    @Test
    fun `abs and sign helpers`() {
        val negative = Money.of("-5.00", Currency.INR)

        assertTrue(negative.isNegative)
        assertEquals(Money.of("5.00", Currency.INR), negative.abs())
    }
}
