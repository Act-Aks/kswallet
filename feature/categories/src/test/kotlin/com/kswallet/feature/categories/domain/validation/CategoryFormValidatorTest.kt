package com.kswallet.feature.categories.domain.validation

import com.kswallet.core.model.CategoryType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * The rules the category editor depends on. They are pure functions, so they are tested
 * directly - no Android, no database, no dispatcher.
 */
class CategoryFormValidatorTest {

    private val input = CategoryFormInput(
        name = "Groceries",
        type = CategoryType.EXPENSE,
        iconKey = "groceries",
        colorArgb = 0xFF1B873F,
    )

    @Test
    fun `valid input produces no errors`() {
        assertEquals(emptyList<CategoryFormError>(), CategoryFormValidator.validate(input) { false })
    }

    @Test
    fun `blank name is rejected`() {
        val errors = CategoryFormValidator.validate(input.copy(name = "   ")) { false }

        assertEquals(listOf(CategoryFormError.EmptyName), errors)
    }

    @Test
    fun `name longer than the limit is rejected`() {
        val errors = CategoryFormValidator.validate(
            input.copy(name = "x".repeat(CategoryFormValidator.MAX_NAME_LENGTH + 1)),
        ) { false }

        assertTrue(errors.contains(CategoryFormError.NameTooLong))
    }

    @Test
    fun `duplicate name is rejected and checked on the trimmed name`() {
        var checked: String? = null

        val errors = CategoryFormValidator.validate(input.copy(name = "  Groceries  ")) { name ->
            checked = name
            true
        }

        assertEquals(listOf(CategoryFormError.DuplicateName), errors)
        assertEquals("Groceries", checked)
    }

    @Test
    fun `a duplicate is not checked when the name itself is invalid`() {
        var duplicateCheckRan = false

        CategoryFormValidator.validate(input.copy(name = "")) {
            duplicateCheckRan = true
            true
        }

        assertEquals(false, duplicateCheckRan)
    }
}
