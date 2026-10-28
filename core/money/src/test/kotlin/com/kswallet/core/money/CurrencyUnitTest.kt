package com.kswallet.core.money

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class CurrencyUnitTest {

    @ParameterizedTest
    @CsvSource(
        "0, 1",
        "1, 10",
        "2, 100",
        "3, 1000",
        "9, 1000000000",
    )
    fun `calculates the minor-unit factor exactly`(
        scale: Int,
        expectedFactor: Long,
    ) {
        val currency = CurrencyUnit(
            code = CurrencyCode.of("ABC"),
            minorUnitScale = scale,
        )

        assertEquals(
            expectedFactor,
            currency.minorUnitsPerMajorUnit,
        )
    }

    @ParameterizedTest
    @ValueSource(ints = [-1, 10, Int.MAX_VALUE])
    fun `rejects unsupported scales`(scale: Int) {
        assertThrows(IllegalArgumentException::class.java) {
            CurrencyUnit(
                code = CurrencyCode.of("ABC"),
                minorUnitScale = scale,
            )
        }
    }

    @Test
    fun `same code with different scale is a different definition`() {
        val first = CurrencyUnit(
            code = CurrencyCode.of("ABC"),
            minorUnitScale = 2,
        )

        val second = first.copy(
            minorUnitScale = 3,
        )

        assertNotEquals(first, second)
    }
}