package com.kswallet.feature.accounts.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class AccountIdTest {

    @Test
    fun `canonical IDs round trip`() {
        val original = AccountId.new()

        assertEquals(
            original,
            AccountId.parse(original.toString()),
        )
    }

    @Test
    fun `uppercase representation is accepted and normalized`() {
        val input = "550E8400-E29B-41D4-A716-446655440000"

        assertEquals(
            "550e8400-e29b-41d4-a716-446655440000",
            AccountId.parse(input).toString(),
        )
    }

    @Test
    fun `abbreviated representation is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            AccountId.parse("1-1-1-1-1")
        }
    }

    @Test
    fun `invalid representation is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            AccountId.parse("not-an-id")
        }
    }
}