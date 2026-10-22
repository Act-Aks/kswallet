package com.kswallet.feature.categories.presentation.list

import com.kswallet.core.common.result.Outcome
import com.kswallet.core.model.Category
import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.CategoryType
import com.kswallet.core.testing.MainDispatcherRule
import com.kswallet.feature.categories.domain.repository.CategoryRepository
import com.kswallet.feature.categories.domain.usecase.DeleteCategoryUseCase
import com.kswallet.feature.categories.domain.usecase.ObserveCategoriesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class CategoriesViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private val repository = mockk<CategoryRepository>()

    private val groceries = Category(
        id = CategoryId(1),
        name = "Groceries",
        type = CategoryType.EXPENSE,
        iconKey = "cart",
        colorArgb = 0xFF112233,
        createdAt = Instant.fromEpochMilliseconds(0),
    )

    private fun viewModel() = CategoriesViewModel(
        observeCategories = ObserveCategoriesUseCase(repository),
        deleteCategory = DeleteCategoryUseCase(repository),
    )

    @Test
    fun `shows the categories of the selected type with their usage`() = runTest(testDispatcher) {
        every { repository.observeCategories(CategoryType.EXPENSE) } returns flowOf(listOf(groceries))
        every { repository.observeUsage() } returns flowOf(mapOf(CategoryId(1) to 4))

        val viewModel = viewModel()

        assertFalse(viewModel.state.value.isLoading)
        assertEquals(1, viewModel.state.value.categories.size)
        assertEquals("Groceries", viewModel.state.value.categories.first().name)
        assertEquals(4, viewModel.state.value.categories.first().transactionCount)
    }

    @Test
    fun `switching the tab reloads the list`() = runTest(testDispatcher) {
        every { repository.observeCategories(CategoryType.EXPENSE) } returns flowOf(listOf(groceries))
        every { repository.observeCategories(CategoryType.INCOME) } returns flowOf(emptyList())
        every { repository.observeUsage() } returns flowOf(emptyMap())

        val viewModel = viewModel()
        viewModel.onAction(CategoriesAction.TypeSelected(CategoryType.INCOME))

        assertEquals(CategoryType.INCOME, viewModel.state.value.selectedType)
        assertEquals(emptyList<CategoryRowUi>(), viewModel.state.value.categories)
    }

    @Test
    fun `deleting asks for confirmation and then deletes`() = runTest(testDispatcher) {
        every { repository.observeCategories(any()) } returns flowOf(listOf(groceries))
        every { repository.observeUsage() } returns flowOf(emptyMap())
        coEvery { repository.deleteCategory(CategoryId(1)) } returns Outcome.Success(Unit)

        val viewModel = viewModel()
        viewModel.onAction(CategoriesAction.DeleteClicked(CategoryId(1)))

        assertEquals(CategoryId(1), viewModel.state.value.pendingDelete?.id)

        viewModel.onAction(CategoriesAction.DeleteConfirmed)

        coVerify { repository.deleteCategory(CategoryId(1)) }
        assertNull(viewModel.state.value.pendingDelete)
    }

    @Test
    fun `dismissing the dialog deletes nothing`() = runTest(testDispatcher) {
        every { repository.observeCategories(any()) } returns flowOf(listOf(groceries))
        every { repository.observeUsage() } returns flowOf(emptyMap())

        val viewModel = viewModel()
        viewModel.onAction(CategoriesAction.DeleteClicked(CategoryId(1)))
        viewModel.onAction(CategoriesAction.DeleteDismissed)

        assertNull(viewModel.state.value.pendingDelete)
        coVerify(exactly = 0) { repository.deleteCategory(any()) }
    }
}
