package com.kswallet.feature.transactions.domain.validation

import com.kswallet.core.model.Account
import com.kswallet.core.model.AccountId
import com.kswallet.core.model.AccountType
import com.kswallet.core.model.Category
import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.CategoryType
import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import com.kswallet.core.model.TransactionType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.time.Instant

/**
 * The invariants of a ledger entry - the rules that keep reports truthful.
 * Transfers across currencies, for instance, are rejected rather than silently rounded.
 */
class TransactionFormValidatorTest {

    private val cash = account(1, "Cash", Currency.INR)
    private val bank = account(2, "Bank", Currency.INR)
    private val dollars = account(3, "USD account", Currency.USD)

    private val groceries = Category(
        id = CategoryId(10),
        name = "Groceries",
        type = CategoryType.EXPENSE,
        iconKey = "cart",
        colorArgb = 0xFF112233,
        createdAt = Instant.fromEpochMilliseconds(0),
    )
    private val salary = Category(
        id = CategoryId(11),
        name = "Salary",
        type = CategoryType.INCOME,
        iconKey = "wallet",
        colorArgb = 0xFF445566,
        createdAt = Instant.fromEpochMilliseconds(0),
    )

    private val accounts = listOf(cash, bank, dollars)
    private val categories = listOf(groceries, salary)

    private fun input(
        type: TransactionType,
        amountText: String = "250.00",
        accountId: AccountId? = cash.id,
        transferAccountId: AccountId? = null,
        categoryId: CategoryId? = groceries.id,
        note: String = "",
    ) = TransactionFormInput(type, amountText, accountId, transferAccountId, categoryId, note)

    @Test
    fun `a well formed expense passes`() {
        assertEquals(
            emptyList<TransactionFormError>(),
            TransactionFormValidator.validate(input(TransactionType.EXPENSE), accounts, categories),
        )
    }

    @Test
    fun `the amount must be a number`() {
        val errors = TransactionFormValidator.validate(
            input(TransactionType.EXPENSE, amountText = "twelve"),
            accounts,
            categories,
        )

        assertTrue(errors.contains(TransactionFormError.InvalidAmount))
    }

    @Test
    fun `the amount must be positive`() {
        val errors = TransactionFormValidator.validate(
            input(TransactionType.EXPENSE, amountText = "0"),
            accounts,
            categories,
        )

        assertTrue(errors.contains(TransactionFormError.AmountNotPositive))
    }

    @Test
    fun `an account is required`() {
        val errors = TransactionFormValidator.validate(
            input(TransactionType.EXPENSE, accountId = null),
            accounts,
            categories,
        )

        assertTrue(errors.contains(TransactionFormError.AccountMissing))
    }

    @Test
    fun `an expense needs a category of the matching kind`() {
        val missing = TransactionFormValidator.validate(
            input(TransactionType.EXPENSE, categoryId = null),
            accounts,
            categories,
        )
        val wrongKind = TransactionFormValidator.validate(
            input(TransactionType.EXPENSE, categoryId = salary.id),
            accounts,
            categories,
        )

        assertTrue(missing.contains(TransactionFormError.CategoryMissing))
        assertTrue(wrongKind.contains(TransactionFormError.CategoryMissing))
    }

    @Test
    fun `an expense does not accept a transfer target`() {
        assertEquals(
            emptyList<TransactionFormError>(),
            TransactionFormValidator.validate(
                input(TransactionType.EXPENSE, transferAccountId = bank.id),
                accounts,
                categories,
            ),
        )
    }

    @Test
    fun `a transfer needs a different target account`() {
        val missing = TransactionFormValidator.validate(
            input(TransactionType.TRANSFER, transferAccountId = null),
            accounts,
            categories,
        )
        val same = TransactionFormValidator.validate(
            input(TransactionType.TRANSFER, transferAccountId = cash.id),
            accounts,
            categories,
        )

        assertTrue(missing.contains(TransactionFormError.TransferTargetMissing))
        assertTrue(same.contains(TransactionFormError.TransferTargetSameAsSource))
    }

    @Test
    fun `a transfer ignores the category`() {
        assertEquals(
            emptyList<TransactionFormError>(),
            TransactionFormValidator.validate(
                input(TransactionType.TRANSFER, transferAccountId = bank.id, categoryId = null),
                accounts,
                categories,
            ),
        )
    }

    @Test
    fun `a transfer across currencies is rejected instead of being rounded`() {
        val errors = TransactionFormValidator.validate(
            input(TransactionType.TRANSFER, transferAccountId = dollars.id),
            accounts,
            categories,
        )

        assertTrue(errors.contains(TransactionFormError.CurrencyMismatch))
    }

    private fun account(id: Long, name: String, currency: Currency) = Account(
        id = AccountId(id),
        name = name,
        type = AccountType.CASH,
        currency = currency,
        balance = Money.zero(currency),
        initialBalance = Money.zero(currency),
        iconKey = "cash",
        colorArgb = 0,
        includedInTotal = true,
        createdAt = Instant.fromEpochMilliseconds(0),
    )
}
