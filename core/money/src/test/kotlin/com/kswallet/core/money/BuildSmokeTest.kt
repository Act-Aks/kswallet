package com.kswallet.core.money

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BuildSmokeTest {

    @Test
    fun `tests execute using the configured Java toolchain`() {
        assertEquals(
            17,
            Runtime.version().feature(),
        )
    }
}