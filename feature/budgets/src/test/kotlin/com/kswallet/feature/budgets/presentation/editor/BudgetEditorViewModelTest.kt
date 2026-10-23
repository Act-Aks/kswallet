package com.kswallet.feature.budgets.presentation.editor

import com.kswallet.core.common.result.DomainError
import com.kswallet.core.common.result.Outcome
import com.kswallet.core.datastore.domain.model.UserSettings
import com.kswallet.core.datastore.domain.repository.SettingsRepository
import com.kswallet.core.model.BudgetId
import com.kswallet.core.model.Category
import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.CategoryType
import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import com.kswallet.core.testing.MainDispatcherExtension
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.feature.budgets.domain.model.Budget
import com.kswallet.feature.budgets.domain.model.BudgetPeriod
import com.kswallet.feature.budgets.domain.repository.BudgetRepository
import com.kswallet.feature.budgets.domain.usecase.ObserveBaseCurrencyUseCase
import com.kswallet.feature.budgets.domain.usecase.ObserveBudgetUseCase
import com.kswallet.feature.budgets.domain.usecase.SaveBudgetUseCase
import com.kswallet.feature.categories.domain.repository.CategoryRepository
import com.kswallet.feature.categories.domain.usecase.ObserveCategoriesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.io.IOException
import kotlin.time.Instant

/**
 * The budget editor: it loads what it edits, validates before writing, and reports failures
 * instead of silently losing the user's input.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BudgetEditorViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @RegisterExtension
    val mainDispatcher = MainDispatcherExtension(testDispatcher)

    private val currency = Currency.INR
    private val repository = mockk<BudgetRepository>()
    private val categoryRepository = mockk<CategoryRepository>()
    private val settingsRepository = mockk<SettingsRepository>()

    private val groceries = Category(
        id = CategoryId(10),
        name = "Groceries",
        type = CategoryType.EXPENSE,
        iconKey = "groceries",
        colorArgb = 0xFF112233,
        createdAt = Instant.fromEpochMilliseconds(0),
    )

    private fun viewModel(budgetIdValue: Long) = BudgetEditorViewModel(
        budgetIdValue = budgetIdValue,
        observeBudget = ObserveBudgetUseCase(repository),
        observeCategories = ObserveCategoriesUseCase(categoryRepository),
        observeBaseCurrency = ObserveBaseCurrencyUseCase(settingsRepository),
        saveBudget = SaveBudgetUseCase(repository),
    )

    private fun stubForm() {
        every { settingsRepository.observeSettings() } returns
            flowOf(UserSettings(defaultCurrency = currency))
        // `ObserveCategoriesUseCase` itself combines the categories with their usage counts,
        // so the editor's two dependencies are stubbed at the repository boundary.
        every { categoryRepository.observeCategories(CategoryType.EXPENSE) } returns
            flowOf(listOf(groceries))
        every { categoryRepository.observeUsage() } returns flowOf(emptyMap())
    }

    @Test
    fun `a new budget opens empty but usable`() = runTest(testDispatcher) {
        stubForm()

        val viewModel = viewModel(budgetIdValue = 0L)

        assertFalse(viewModel.state.value.isLoading)
        assertTrue(viewModel.state.value.isNew)
        assertEquals(currency, viewModel.state.value.currency)
        assertEquals(BudgetPeriod.MONTHLY, viewModel.state.value.period)
        assertEquals(listOf("Groceries"), viewModel.state.value.categories.map { it.name })
    }

    @Test
    fun `an existing budget loads its category, limit and period`() = runTest(testDispatcher) {
        stubForm()
        every { repository.observeBudget(BudgetId(4)) } returns flowOf(
            Budget(
                id = BudgetId(4),
                categoryId = CategoryId(10),
                limit = Money.of("5000.00", currency),
                period = BudgetPeriod.YEARLY,
            ),
        )

        val viewModel = viewModel(budgetIdValue = 4L)

        assertFalse(viewModel.state.value.isLoading)
        assertFalse(viewModel.state.value.isNew)
        assertEquals(CategoryId(10), viewModel.state.value.categoryId)
        assertEquals("Groceries", viewModel.state.value.categoryName)
        assertEquals("5000.00", viewModel.state.value.limitText)
        assertEquals(BudgetPeriod.YEARLY, viewModel.state.value.period)
    }

    @Test
    fun `saving a valid form writes the budget and goes back`() = runTest(testDispatcher) {
        stubForm()
        coEvery { repository.upsertBudget(any()) } returns Outcome.Success(BudgetId(9))
        val viewModel = viewModel(budgetIdValue = 0L)

        viewModel.onAction(BudgetEditorAction.CategorySelected(CategoryId(10)))
        viewModel.onAction(BudgetEditorAction.LimitChanged("2500"))
        viewModel.onAction(BudgetEditorAction.SaveClicked)

        coVerify {
            repository.upsertBudget(
                match { budget ->
                    budget.categoryId == CategoryId(10) &&
                        budget.limit == Money.of("2500.00", currency) &&
                        budget.period == BudgetPeriod.MONTHLY
                },
            )
        }
        assertEquals(BudgetEditorEvent.NavigateBack(saved = true), viewModel.events.first())
        assertFalse(viewModel.state.value.isSaving)
    }

    @Test
    fun `saving without a category shows the rule, not a storage error`() = runTest(testDispatcher) {
        stubForm()
        val viewModel = viewModel(budgetIdValue = 0L)

        viewModel.onAction(BudgetEditorAction.LimitChanged("2500"))
        viewModel.onAction(BudgetEditorAction.SaveClicked)

        assertEquals(BudgetEditorError.CategoryMissing, viewModel.state.value.error)
        coVerify(exactly = 0) { repository.upsertBudget(any()) }
    }

    @Test
    fun `a zero limit is rejected as not positive`() = runTest(testDispatcher) {
        stubForm()
        val viewModel = viewModel(budgetIdValue = 0L)

        viewModel.onAction(BudgetEditorAction.CategorySelected(CategoryId(10)))
        viewModel.onAction(BudgetEditorAction.LimitChanged("0"))
        viewModel.onAction(BudgetEditorAction.SaveClicked)

        assertEquals(BudgetEditorError.LimitNotPositive, viewModel.state.value.error)
        coVerify(exactly = 0) { repository.upsertBudget(any()) }
    }

    @Test
    fun `the typed amount is sanitised for the currency`() = runTest(testDispatcher) {
        stubForm()
        val viewModel = viewModel(budgetIdValue = 0L)

        viewModel.onAction(BudgetEditorAction.LimitChanged("25a00.9z"))

        assertEquals("2500.9", viewModel.state.value.limitText)
    }

    @Test
    fun `a storage failure is reported and the editor stays open`() = runTest(testDispatcher) {
        stubForm()
        coEvery { repository.upsertBudget(any()) } returns
            Outcome.Failure(DomainError.Data(IOException("disk full")))
        val viewModel = viewModel(budgetIdValue = 0L)

        viewModel.onAction(BudgetEditorAction.CategorySelected(CategoryId(10)))
        viewModel.onAction(BudgetEditorAction.LimitChanged("2500"))
        viewModel.onAction(BudgetEditorAction.SaveClicked)

        assertEquals(
            BudgetEditorEvent.ShowMessage(
                UiMessage.Resource(com.kswallet.feature.budgets.R.string.error_storage),
            ),
            viewModel.events.first(),
        )
        assertFalse(viewModel.state.value.isSaving)
    }

    @Test
    fun `editing keeps the budget's identity`() = runTest(testDispatcher) {
        stubForm()
        every { repository.observeBudget(BudgetId(4)) } returns flowOf(
            Budget(id = BudgetId(4), categoryId = CategoryId(10), limit = Money.of("5000.00", currency)),
        )
        coEvery { repository.upsertBudget(any()) } returns Outcome.Success(BudgetId(4))
        val viewModel = viewModel(budgetIdValue = 4L)

        viewModel.onAction(BudgetEditorAction.LimitChanged("6000"))
        viewModel.onAction(BudgetEditorAction.SaveClicked)

        coVerify { repository.upsertBudget(match { it.id == BudgetId(4) }) }
    }
}
