package com.kswallet.core.currencies

import com.kswallet.core.money.CurrencyCode
import com.kswallet.core.money.Money
import com.kswallet.core.money.MoneyParseResult
import com.kswallet.core.money.MoneyParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class KsWalletCurrenciesTest {

    @Test
    fun `initial supported set is explicit and sorted`() {
        val registry = createKsWalletCurrencyRegistry()

        assertEquals(
            listOf(
                "EUR",
                "INR",
                "JPY",
                "KWD",
                "USD",
            ),
            registry.supportedCurrencies().map {
                it.code.value
            },
        )
    }

    @ParameterizedTest
    @CsvSource(
        "EUR, 2",
        "INR, 2",
        "JPY, 0",
        "KWD, 3",
        "USD, 2",
    )
    fun `supported currency has the declared scale`(
        code: String,
        expectedScale: Int,
    ) {
        val registry = createKsWalletCurrencyRegistry()

        val definition = registry.find(
            CurrencyCode.of(code),
        )

        assertNotNull(definition)

        assertEquals(
            expectedScale,
            requireNotNull(definition).minorUnitScale,
        )
    }

    @Test
    fun `unsupported code does not fall back to another currency`() {
        val registry = createKsWalletCurrencyRegistry()

        assertNull(
            registry.find(CurrencyCode.of("ABC")),
        )
    }

    @ParameterizedTest
    @CsvSource(
        "JPY, 125, 125",
        "INR, 125.50, 12550",
        "KWD, 125.500, 125500",
    )
    fun `parser uses scale supplied by registry`(
        code: String,
        input: String,
        expectedMinorUnits: Long,
    ) {
        val registry = createKsWalletCurrencyRegistry()

        val definition = requireNotNull(
            registry.find(CurrencyCode.of(code)),
        )

        assertEquals(
            MoneyParseResult.Success(
                Money(
                    minorUnits = expectedMinorUnits,
                    currency = definition,
                ),
            ),
            MoneyParser.parseCanonical(
                input = input,
                currency = definition,
            ),
        )
    }
}