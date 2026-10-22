package com.kswallet.feature.transactions.presentation.list

import com.kswallet.core.model.AccountId
import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import com.kswallet.core.model.Transaction
import com.kswallet.core.model.TransactionId
import com.kswallet.core.model.TransactionType
import com.kswallet.feature.transactions.domain.model.TransactionDetails
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.time.Instant

/**
 * Grouping rules are the part of a ledger screen that goes wrong quietly: an entry shown
 * under the wrong day, or a transfer counted as spending. They are pure, so they are tested.
 */
class TransactionListMapperTest {

    private val zone = TimeZone.of("Asia/Kolkata")
    private val today = LocalDate.parse("2026-10-12")
    private val currency = Currency.INR

    private fun details(
        id: Long,
        type: TransactionType,
        amount: String,
        date: String,
        category: String? = "Groceries",
        account: String = "Cash",
        transferAccount: String? = null,
    ) = TransactionDetails(
        transaction = Transaction(
            id = TransactionId(id),
            type = type,
            amount = Money.of(amount, currency),
            accountId = AccountId(1),
            transferAccountId = transferAccount?.let { AccountId(2) },
            categoryId = category?.let { CategoryId(10) },
            dateTime = Instant.parse("${date}T06:30:00Z"),
            note = null,
            createdAt = Instant.parse("${date}T06:30:00Z"),
        ),
        accountName = account,
        transferAccountName = transferAccount,
        categoryName = category,
        categoryIconKey = "cart",
        categoryColorArgb = 0xFF112233,
    )

    @Test
    fun `entries of the same day are grouped under one header`() {
        val groups = TransactionListMapper.toDayGroups(
            details = listOf(
                details(1, TransactionType.EXPENSE, "100.00", "2026-10-12"),
                details(2, TransactionType.EXPENSE, "50.00", "2026-10-12"),
                details(3, TransactionType.EXPENSE, "20.00", "2026-10-11"),
            ),
            today = today,
            timeZone = zone,
            currency = currency,
        )

        assertEquals(2, groups.size)
        assertEquals("Today", groups.first().header)
        assertEquals("Yesterday", groups[1].header)
        assertEquals(2, groups.first().items.size)
    }

    @Test
    fun `a day total nets income against expenses`() {
        val total = TransactionListMapper.dayTotal(
            listOf(
                details(1, TransactionType.INCOME, "1000.00", "2026-10-12", category = "Salary"),
                details(2, TransactionType.EXPENSE, "250.00", "2026-10-12"),
            ),
            currency,
        )

        assertEquals(Money.of("750.00", currency), total)
    }

    @Test
    fun `transfers cancel out of a day total`() {
        val total = TransactionListMapper.dayTotal(
            listOf(
                details(1, TransactionType.EXPENSE, "250.00", "2026-10-12"),
                details(
                    2,
                    TransactionType.TRANSFER,
                    "500.00",
                    "2026-10-12",
                    category = null,
                    transferAccount = "Bank",
                ),
            ),
            currency,
        )

        assertEquals(Money.of("-250.00", currency), total)
    }

    @Test
    fun `a transfer reads as source to target`() {
        val item = TransactionListMapper.toItem(
            details(
                1,
                TransactionType.TRANSFER,
                "500.00",
                "2026-10-12",
                category = null,
                account = "Cash",
                transferAccount = "Bank",
            ),
        )

        assertEquals("Bank", item.title)
        assertEquals("Cash \u2192 Bank", item.subtitle)
    }

    @Test
    fun `a note wins over the account name as subtitle`() {
        val withNote = details(1, TransactionType.EXPENSE, "10.00", "2026-10-12")
            .let { it.copy(transaction = it.transaction.copy(note = "lunch")) }

        assertEquals("lunch", TransactionListMapper.toItem(withNote).subtitle)
    }
}
