package com.kswallet.feature.accounts.domain.validation

import com.kswallet.core.model.AccountType
import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AccountFormValidatorTest {

    private val input = AccountFormInput(
        name = "HDFC Savings",
        type = AccountType.BANK,
        currency = Currency.INR,
        initialBalanceText = "1500.50",
        colorArgb = 0xFF0E7C66,
        includedInTotal = true,
    )

    @Test
    fun `valid form produces no errors`() {
        assertEquals(emptyList<AccountFormError>(), AccountFormValidator.validate(input))
    }

    @Test
    fun `name is required`() {
        assertEquals(
            listOf(AccountFormError.EmptyName),
            AccountFormValidator.validate(input.copy(name = "  ")),
        )
    }

    @Test
    fun `name is length limited`() {
        val errors = AccountFormValidator.validate(
            input.copy(name = "a".repeat(AccountFormValidator.MAX_NAME_LENGTH + 1)),
        )

        assertTrue(errors.contains(AccountFormError.NameTooLong))
    }

    @Test
    fun `a blank opening balance means zero, not an error`() {
        val blank = input.copy(initialBalanceText = "")

        assertEquals(emptyList<AccountFormError>(), AccountFormValidator.validate(blank))
        assertEquals(Money.zero(Currency.INR), AccountFormValidator.parseInitialBalance(blank))
    }

    @Test
    fun `a non numeric opening balance is rejected`() {
        val errors = AccountFormValidator.validate(input.copy(initialBalanceText = "abc"))

        assertTrue(errors.contains(AccountFormError.InvalidInitialBalance))
    }

    @Test
    fun `the opening balance is parsed in the account currency`() {
        val parsed = AccountFormValidator.parseInitialBalance(input)

        assertEquals(Money.of("1500.50", Currency.INR), parsed)
        assertEquals(150050L, parsed?.minorUnits)
    }
}
