package com.kswallet.feature.categories.domain.usecase

import com.kswallet.core.common.result.DomainError
import com.kswallet.core.common.result.Outcome
import com.kswallet.core.model.Category
import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.CategoryType
import com.kswallet.core.testing.FixedTimeProvider
import com.kswallet.feature.categories.domain.repository.CategoryRepository
import com.kswallet.feature.categories.domain.validation.CategoryFormError
import com.kswallet.feature.categories.domain.validation.CategoryFormInput
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.time.Instant

class SaveCategoryUseCaseTest {

    private val repository = mockk<CategoryRepository>()
    private val timeProvider = FixedTimeProvider.of("2026-10-12")
    private val useCase = SaveCategoryUseCase(repository, timeProvider)

    private val input = CategoryFormInput(
        name = "Groceries",
        type = CategoryType.EXPENSE,
        iconKey = "cart",
        colorArgb = 0xFF112233,
    )

    @Test
    fun `saves a new category with a generated creation time`() = runTest {
        coEvery { repository.countCategoriesWithName(any(), any(), any()) } returns Outcome.Success(0)
        coEvery { repository.upsertCategory(any()) } returns Outcome.Success(CategoryId(7))

        val result = useCase(input, existing = null)

        assertEquals(SaveCategoryResult.Saved(CategoryId(7)), result)
        coVerify {
            repository.upsertCategory(
                match {
                    it.id == CategoryId.UNSAVED &&
                        it.name == "Groceries" &&
                        it.createdAt == timeProvider.now()
                },
            )
        }
    }

    @Test
    fun `keeps the id and creation time when editing`() = runTest {
        val existing = Category(
            id = CategoryId(4),
            name = "Food",
            type = CategoryType.EXPENSE,
            iconKey = "cart",
            colorArgb = 0xFF112233,
            createdAt = Instant.fromEpochMilliseconds(1_000L),
        )
        coEvery { repository.countCategoriesWithName(any(), any(), CategoryId(4)) } returns Outcome.Success(0)
        coEvery { repository.upsertCategory(any()) } returns Outcome.Success(CategoryId(4))

        useCase(input, existing = existing)

        coVerify {
            repository.upsertCategory(
                match { it.id == CategoryId(4) && it.createdAt == existing.createdAt },
            )
        }
    }

    @Test
    fun `rejects a duplicate name before touching the database`() = runTest {
        coEvery { repository.countCategoriesWithName(any(), any(), any()) } returns Outcome.Success(1)

        val result = useCase(input.copy(name = "Food"), existing = null)

        assertEquals(SaveCategoryResult.Invalid(listOf(CategoryFormError.DuplicateName)), result)
        coVerify(exactly = 0) { repository.upsertCategory(any()) }
    }

    @Test
    fun `reports a storage failure instead of throwing`() = runTest {
        coEvery { repository.countCategoriesWithName(any(), any(), any()) } returns Outcome.Success(0)
        coEvery { repository.upsertCategory(any()) } returns Outcome.Failure(DomainError.Data(IllegalStateException("disk full")))

        val result = useCase(input, existing = null)

        assertTrue(result is SaveCategoryResult.Failed)
    }

    @Test
    fun `a failing duplicate check does not block the save`() = runTest {
        coEvery { repository.countCategoriesWithName(any(), any(), any()) } returns
            Outcome.Failure(DomainError.Data(IllegalStateException("query failed")))
        coEvery { repository.upsertCategory(any()) } returns Outcome.Success(CategoryId(9))

        val result = useCase(input, existing = null)

        assertEquals(SaveCategoryResult.Saved(CategoryId(9)), result)
    }
}
