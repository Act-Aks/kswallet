package com.kswallet.feature.transactions.domain.model

import com.kswallet.core.identity.AccountId
import com.kswallet.core.identity.TransactionId
import com.kswallet.core.money.CurrencyCode
import com.kswallet.core.money.CurrencyUnit
import com.kswallet.core.money.Money
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.time.Instant

class TransactionTest {

    private val currency = CurrencyUnit(
        code = CurrencyCode.of("INR"),
        minorUnitScale = 2,
    )

    @Test
    fun `income keeps a positive signed amount`() {
        val transaction = transaction(
            type = TransactionType.Income,
            minorUnits = 12_550,
        )

        assertEquals(
            Money(12_550, currency),
            transaction.signedAmount,
        )
    }

    @Test
    fun `expense negates a positive stored amount`() {
        val transaction = transaction(
            type = TransactionType.Expense,
            minorUnits = 12_550,
        )

        assertEquals(
            Money(-12_550, currency),
            transaction.signedAmount,
        )
    }

    @Test
    fun `zero amount is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            transaction(
                type = TransactionType.Expense,
                minorUnits = 0,
            )
        }
    }

    @Test
    fun `negative amount is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            transaction(
                type = TransactionType.Expense,
                minorUnits = -1,
            )
        }
    }

    @Test
    fun `blank note becomes null`() {
        val note = TransactionNote.of("   ")

        assertNull(note)
    }

    @Test
    fun `note is trimmed`() {
        val note = TransactionNote.of("  Grocery shopping  ")

        assertNotNull(note)
        assertEquals(
            "Grocery shopping",
            requireNotNull(note).value,
        )
    }

    @Test
    fun `overlong note is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            TransactionNote.of("a".repeat(TransactionNote.MAX_LENGTH + 1))
        }
    }

    @Test
    fun `future timestamp is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            TransactionTimestamp.of(
                Instant.now().plusSeconds(120),
            )
        }
    }

    private fun transaction(
        type: TransactionType,
        minorUnits: Long,
    ): Transaction = Transaction(
        id = TransactionId.new(),
        accountId = AccountId.parse(
            java.util.UUID.randomUUID().toString(),
        ),
        type = type,
        amount = Money(minorUnits, currency),
        timestamp = TransactionTimestamp.now(),
        note = TransactionNote.of("Test"),
    )
}