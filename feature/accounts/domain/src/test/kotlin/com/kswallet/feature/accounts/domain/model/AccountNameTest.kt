package com.kswallet.feature.accounts.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class AccountNameTest {

    @Test
    fun `trims surrounding whitespace and preserves internal text`() {
        val result = AccountName.parse("  Travel  Fund  ")

        val valid = assertInstanceOf(
            AccountNameResult.Valid::class.java,
            result,
        )

        assertEquals(
            "Travel  Fund",
            valid.name.value,
        )
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "\t\n"])
    fun `rejects empty normalized names`(input: String) {
        assertEquals(
            AccountNameResult.Invalid(AccountNameError.Empty),
            AccountName.parse(input),
        )
    }

    @Test
    fun `accepts the maximum length`() {
        val result = AccountName.parse(
            "A".repeat(AccountName.MAX_LENGTH),
        )

        assertInstanceOf(
            AccountNameResult.Valid::class.java,
            result,
        )
    }

    @Test
    fun `rejects names exceeding the maximum length`() {
        assertEquals(
            AccountNameResult.Invalid(AccountNameError.TooLong),
            AccountName.parse(
                "A".repeat(AccountName.MAX_LENGTH + 1),
            ),
        )
    }

    @Test
    fun `rejects internal control characters`() {
        assertEquals(
            AccountNameResult.Invalid(
                AccountNameError.ControlCharacter,
            ),
            AccountName.parse("Travel\nFund"),
        )
    }

    @Test
    fun `preserves Unicode names`() {
        val result = AccountName.parse("旅行資金")

        val valid = assertInstanceOf(
            AccountNameResult.Valid::class.java,
            result,
        )

        assertEquals("旅行資金", valid.name.value)
    }
}