package com.kswallet.core.currencies

import com.kswallet.core.money.CurrencyCode
import com.kswallet.core.money.CurrencyUnit
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class StaticCurrencyRegistryTest {

    private fun currency(
        code: String,
        scale: Int = 2,
    ): CurrencyUnit =
        CurrencyUnit(
            code = CurrencyCode.of(code),
            minorUnitScale = scale,
        )

    @Test
    fun `find returns the registered definition`() {
        val expected = currency("ABC", scale = 3)

        val registry = StaticCurrencyRegistry(
            listOf(expected),
        )

        assertEquals(
            expected,
            registry.find(CurrencyCode.of("ABC")),
        )
    }

    @Test
    fun `unknown code returns null`() {
        val registry = StaticCurrencyRegistry(
            listOf(currency("ABC")),
        )

        assertNull(
            registry.find(CurrencyCode.of("XYZ")),
        )
    }

    @Test
    fun `lookup works with normalized codes`() {
        val expected = currency("ABC")

        val registry = StaticCurrencyRegistry(
            listOf(expected),
        )

        assertEquals(
            expected,
            registry.find(CurrencyCode.of(" abc ")),
        )
    }

    @Test
    fun `supported currencies are sorted by code`() {
        val registry = StaticCurrencyRegistry(
            listOf(
                currency("XYZ"),
                currency("ABC"),
                currency("MNO"),
            ),
        )

        assertEquals(
            listOf("ABC", "MNO", "XYZ"),
            registry.supportedCurrencies().map {
                it.code.value
            },
        )
    }

    @Test
    fun `identical duplicate definitions are rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            StaticCurrencyRegistry(
                listOf(
                    currency("ABC", scale = 2),
                    currency("ABC", scale = 2),
                ),
            )
        }
    }

    @Test
    fun `conflicting definitions are rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            StaticCurrencyRegistry(
                listOf(
                    currency("ABC", scale = 2),
                    currency("ABC", scale = 3),
                ),
            )
        }
    }

    @Test
    fun `empty registry is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            StaticCurrencyRegistry(emptyList())
        }
    }

    @Test
    fun `mutating source collection does not change registry`() {
        val expected = currency("ABC")

        val source = mutableListOf(expected)

        val registry = StaticCurrencyRegistry(source)

        source.clear()
        source.add(currency("XYZ"))

        assertEquals(
            listOf(expected),
            registry.supportedCurrencies(),
        )

        assertNull(
            registry.find(CurrencyCode.of("XYZ")),
        )
    }

    @Test
    fun `mutating a returned snapshot does not change registry`() {
        val expected = currency("ABC")

        val registry = StaticCurrencyRegistry(
            listOf(expected),
        )

        val snapshot = registry
            .supportedCurrencies()
            .toMutableList()

        snapshot.clear()
        snapshot.add(currency("XYZ"))

        assertEquals(
            listOf(expected),
            registry.supportedCurrencies(),
        )
    }
}