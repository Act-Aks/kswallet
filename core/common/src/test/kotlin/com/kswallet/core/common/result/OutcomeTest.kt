package com.kswallet.core.common.result

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class OutcomeTest {

    @Test
    fun `map transforms a success`() {
        val result = Outcome.Success(21).map { it * 2 }

        assertEquals(42, result.valueOrNull())
    }

    @Test
    fun `map keeps the failure untouched`() {
        val failure: Outcome<Int> = Outcome.Failure(DomainError.NotFound)

        assertEquals(DomainError.NotFound, failure.map { it * 2 }.errorOrNull())
    }

    @Test
    fun `fold collapses both branches`() {
        val message = Outcome.Failure(DomainError.Conflict("account has transactions"))
            .fold(onSuccess = { "ok" }, onFailure = { it.debugMessage() })

        assertEquals("account has transactions", message)
    }

    @Test
    fun `onFailure callback only runs for failures`() {
        var called = 0
        Outcome.Success("value").onFailure { called++ }
        assertNull(null)
        assertEquals(0, called)

        Outcome.Failure(DomainError.NotFound).onFailure { called++ }
        assertEquals(1, called)
        assertInstanceOf(Outcome.Failure::class.java, Outcome.Failure(DomainError.NotFound))
    }
}
