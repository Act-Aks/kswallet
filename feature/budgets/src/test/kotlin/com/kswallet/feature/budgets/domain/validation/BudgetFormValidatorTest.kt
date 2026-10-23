package com.kswallet.feature.budgets.domain.validation

import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.Currency
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * The rules of a budget form. They are the part of the feature that is finished, so they are
 * the part that is tested.
 */
class BudgetFormValidatorTest {

    private val currency = Currency.INR

    @Test
    fun `a category and a positive amount pass`() {
        val errors = BudgetFormValidator.validate(
            BudgetFormInput(categoryId = CategoryId(1), limitText = "5000"),
            currency,
        )

        assertTrue(errors.isEmpty())
    }

    @Test
    fun `a budget without a category is rejected`() {
        val errors = BudgetFormValidator.validate(
            BudgetFormInput(categoryId = null, limitText = "5000"),
            currency,
        )

        assertEquals(listOf(BudgetFormError.CategoryMissing), errors)
    }

    @Test
    fun `an unparsable amount is rejected once, not twice`() {
        val errors = BudgetFormValidator.validate(
            BudgetFormInput(categoryId = CategoryId(1), limitText = "abc"),
            currency,
        )

        assertEquals(listOf(BudgetFormError.InvalidLimit), errors)
    }

    @Test
    fun `a zero limit is rejected as not positive`() {
        val errors = BudgetFormValidator.validate(
            BudgetFormInput(categoryId = CategoryId(1), limitText = "0"),
            currency,
        )

        assertEquals(listOf(BudgetFormError.LimitNotPositive), errors)
    }

    @Test
    fun `a negative limit is reported as not positive`() {
        val errors = BudgetFormValidator.validate(
            BudgetFormInput(categoryId = CategoryId(1), limitText = "-10"),
            currency,
        )

        assertEquals(listOf(BudgetFormError.LimitNotPositive), errors)
    }
}
