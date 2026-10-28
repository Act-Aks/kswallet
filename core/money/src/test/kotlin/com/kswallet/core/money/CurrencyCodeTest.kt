package com.kswallet.core.money

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class CurrencyCodeTest {

    @Test
    fun `normalizes lowercase and surrounding whitespace`() {
        val code = CurrencyCode.of("  inr  ")

        assertEquals("INR", code.value)
        assertEquals("INR", code.toString())
    }

    @Test
    fun `canonical codes have value equality`() {
        assertEquals(
            CurrencyCode.of("usd"),
            CurrencyCode.of("USD"),
        )
    }

    @Test
    fun `validates syntax without claiming registry membership`() {
        assertEquals(
            "ABC",
            CurrencyCode.of("ABC").value,
        )
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "",
            " ",
            "US",
            "USDD",
            "U1D",
            "US$",
            "U D",
            "€UR",
        ],
    )
    fun `rejects invalid syntax`(input: String) {
        assertThrows(IllegalArgumentException::class.java) {
            CurrencyCode.of(input)
        }
    }
}