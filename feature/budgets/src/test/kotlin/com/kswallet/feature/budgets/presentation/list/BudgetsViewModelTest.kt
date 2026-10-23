package com.kswallet.feature.budgets.presentation.list

import com.kswallet.core.common.result.DomainError
import com.kswallet.core.common.result.Outcome
import com.kswallet.core.model.BudgetId
import com.kswallet.core.model.Category
import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.CategoryType
import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import com.kswallet.core.testing.MainDispatcherExtension
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.feature.budgets.R
import com.kswallet.feature.budgets.domain.model.Budget
import com.kswallet.feature.budgets.domain.model.BudgetPeriod
import com.kswallet.feature.budgets.domain.model.BudgetProgress
import com.kswallet.feature.budgets.domain.repository.BudgetRepository
import com.kswallet.feature.budgets.domain.usecase.DeleteBudgetUseCase
import com.kswallet.feature.budgets.domain.usecase.ObserveBudgetsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class BudgetsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @RegisterExtension
    val mainDispatcher = MainDispatcherExtension(testDispatcher)

    private val repository = mockk<BudgetRepository>()
    private val currency = Currency.INR

    private fun viewModel() = BudgetsViewModel(
        observeBudgets = ObserveBudgetsUseCase(repository),
        deleteBudget = DeleteBudgetUseCase(repository),
    )

    private fun progress(
        id: Long,
        category: String = "Groceries",
        limit: String,
        spent: String,
        period: BudgetPeriod = BudgetPeriod.MONTHLY,
    ) = BudgetProgress(
        budget = Budget(
            id = BudgetId(id),
            categoryId = CategoryId(id * 10),
            limit = Money.of(limit, currency),
            period = period,
        ),
        category = Category(
            id = CategoryId(id * 10),
            name = category,
            type = CategoryType.EXPENSE,
            iconKey = "groceries",
            colorArgb = 0xFF112233,
            createdAt = Instant.fromEpochMilliseconds(0),
        ),
        spent = Money.of(spent, currency),
    )

    @Test
    fun `an empty database is the empty state, with the call to action`() = runTest(testDispatcher) {
        every { repository.observeBudgets() } returns flowOf(emptyList())

        val viewModel = viewModel()

        assertFalse(viewModel.state.value.isLoading)
        assertTrue(viewModel.state.value.isEmpty)
    }

    @Test
    fun `rows carry the category, the money and the fraction used`() = runTest(testDispatcher) {
        every { repository.observeBudgets() } returns flowOf(
            listOf(
                progress(1, limit = "1000.00", spent = "400.00"),
                progress(2, category = "Transport", limit = "500.00", spent = "800.00"),
            ),
        )

        val viewModel = viewModel()

        val under = viewModel.state.value.budgets.first()
        assertEquals("Groceries", under.categoryName)
        assertEquals(0.4f, under.fractionUsed, 0.001f)
        assertEquals(BudgetPeriodLabel.MONTHLY, under.periodLabel)
        assertFalse(under.isOverBudget)

        val over = viewModel.state.value.budgets[1]
        assertEquals(1f, over.fractionUsed, 0.001f)
        assertTrue(over.isOverBudget)
        // 800 spent against a 500 limit: the overshoot is what the row reports.
        assertEquals(Money.of("300.00", currency), over.spent - over.limit)
    }

    @Test
    fun `the period of a budget reaches the row as a label, not as an enum from domain`() =
        runTest(testDispatcher) {
            every { repository.observeBudgets() } returns flowOf(
                listOf(progress(1, limit = "100.00", spent = "10.00", period = BudgetPeriod.WEEKLY)),
            )

            val viewModel = viewModel()

            assertEquals(BudgetPeriodLabel.WEEKLY, viewModel.state.value.budgets.first().periodLabel)
        }

    @Test
    fun `tapping a budget asks to open its editor`() = runTest(testDispatcher) {
        every { repository.observeBudgets() } returns flowOf(listOf(progress(7, limit = "1000.00", spent = "0.00")))
        val viewModel = viewModel()

        viewModel.onAction(BudgetsAction.BudgetClicked(BudgetId(7)))

        assertEquals(BudgetsEvent.OpenEditor(BudgetId(7)), viewModel.events.first())
    }

    @Test
    fun `adding a budget opens the editor without an id`() = runTest(testDispatcher) {
        every { repository.observeBudgets() } returns flowOf(emptyList())
        val viewModel = viewModel()

        viewModel.onAction(BudgetsAction.AddBudgetClicked)

        assertEquals(BudgetsEvent.OpenEditor(null), viewModel.events.first())
    }

    @Test
    fun `deleting asks for confirmation first and then deletes`() = runTest(testDispatcher) {
        every { repository.observeBudgets() } returns flowOf(listOf(progress(1, limit = "1000.00", spent = "0.00")))
        coEvery { repository.deleteBudget(BudgetId(1)) } returns Outcome.Success(Unit)
        val viewModel = viewModel()

        viewModel.onAction(BudgetsAction.DeleteClicked(BudgetId(1)))

        assertEquals(BudgetId(1), viewModel.state.value.pendingDelete?.id)
        assertEquals("Groceries", viewModel.state.value.pendingDelete?.categoryName)
        coVerify(exactly = 0) { repository.deleteBudget(any()) }

        viewModel.onAction(BudgetsAction.DeleteConfirmed)

        coVerify { repository.deleteBudget(BudgetId(1)) }
        assertNull(viewModel.state.value.pendingDelete)
    }

    @Test
    fun `dismissing the confirmation keeps the budget`() = runTest(testDispatcher) {
        every { repository.observeBudgets() } returns flowOf(listOf(progress(1, limit = "1000.00", spent = "0.00")))
        val viewModel = viewModel()

        viewModel.onAction(BudgetsAction.DeleteClicked(BudgetId(1)))
        viewModel.onAction(BudgetsAction.DeleteDismissed)

        assertNull(viewModel.state.value.pendingDelete)
        coVerify(exactly = 0) { repository.deleteBudget(any()) }
    }

    @Test
    fun `a failed delete is reported`() = runTest(testDispatcher) {
        every { repository.observeBudgets() } returns flowOf(listOf(progress(1, limit = "1000.00", spent = "0.00")))
        coEvery { repository.deleteBudget(any()) } returns Outcome.Failure(DomainError.NotFound)
        val viewModel = viewModel()

        viewModel.onAction(BudgetsAction.DeleteClicked(BudgetId(1)))
        viewModel.onAction(BudgetsAction.DeleteConfirmed)

        assertEquals(
            BudgetsEvent.ShowMessage(UiMessage.Resource(R.string.error_not_found)),
            viewModel.events.first(),
        )
    }

    @Test
    fun `a budget deleted elsewhere closes its confirmation dialog`() = runTest(testDispatcher) {
        val rows = MutableStateFlow(listOf(progress(1, limit = "1000.00", spent = "0.00")))
        every { repository.observeBudgets() } returns rows
        val viewModel = viewModel()

        viewModel.onAction(BudgetsAction.DeleteClicked(BudgetId(1)))
        rows.value = emptyList()

        assertNull(viewModel.state.value.pendingDelete)
        assertTrue(viewModel.state.value.isEmpty)
    }
}
