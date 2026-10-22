package com.kswallet.feature.reports.presentation.reports

import com.kswallet.core.common.time.TimeProvider
import com.kswallet.core.datastore.domain.model.UserSettings
import com.kswallet.core.datastore.domain.repository.SettingsRepository
import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import com.kswallet.core.testing.FixedTimeProvider
import com.kswallet.core.testing.MainDispatcherRule
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.feature.reports.R
import com.kswallet.feature.reports.domain.model.CategoryReportRow
import com.kswallet.feature.reports.domain.model.MonthlyReport
import com.kswallet.feature.reports.domain.model.ReportMonth
import com.kswallet.feature.reports.domain.repository.ReportRepository
import com.kswallet.feature.reports.domain.usecase.ObserveBaseCurrencyUseCase
import com.kswallet.feature.reports.domain.usecase.ObserveMonthlyReportUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

/**
 * The reports screen is one query and a lot of presentation, so these tests pin the decisions
 * that are the screen's own: which month is asked for, that the future is not reachable, what
 * happens when the query fails, and what the retry does.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ReportsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private val currency = Currency.INR
    private val repository = mockk<ReportRepository>()
    private val settingsRepository = mockk<SettingsRepository>()
    private val timeProvider: TimeProvider = FixedTimeProvider.of("2026-10-12", TimeZone.of("Asia/Kolkata"))

    /** October 2026 is the month the fixed clock stands in, and it is already 12 days old. */
    private val october = ReportMonth(2026, 10)
    private val september = ReportMonth(2026, 9)

    private fun viewModel() = ReportsViewModel(
        timeProvider = timeProvider,
        observeBaseCurrency = ObserveBaseCurrencyUseCase(settingsRepository),
        observeMonthlyReport = ObserveMonthlyReportUseCase(repository),
    )

    private fun stubSettings(currency: Currency = this.currency) {
        every { settingsRepository.observeSettings() } returns flowOf(UserSettings(defaultCurrency = currency))
    }

    private fun report(
        month: ReportMonth = october,
        currency: Currency = this.currency,
        income: String = "2000.00",
        expense: String = "1200.00",
        previousMonthExpense: String = "1000.00",
        daysElapsed: Int = 12,
        categories: List<CategoryReportRow> = emptyList(),
    ) = MonthlyReport(
        month = month,
        currency = currency,
        income = Money.of(income, currency),
        expense = Money.of(expense, currency),
        previousMonthExpense = Money.of(previousMonthExpense, currency),
        categories = categories,
        dailyExpenses = List(month.daysInMonth) { Money.zero(currency) },
        daysElapsed = daysElapsed,
    )

    @Test
    fun `the screen opens on the current month in the user's currency`() = runTest(testDispatcher) {
        stubSettings()
        every { repository.observeMonthlyReport(october, currency) } returns flowOf(report())

        val viewModel = viewModel()

        val state = viewModel.state.value
        assertEquals(october, state.month)
        assertEquals(currency, state.currency)
        assertFalse(state.isLoading)
        // There is nothing to the right of the current month.
        assertFalse(state.canGoNext)
        assertTrue(state.isCurrentMonth)
    }

    @Test
    fun `the summary numbers come from the domain, not recomputed by the screen`() =
        runTest(testDispatcher) {
            stubSettings()
            every { repository.observeMonthlyReport(october, currency) } returns flowOf(report())

            val state = viewModel().state.value

            assertEquals(Money.of("1200.00", currency), state.expense)
            assertEquals(Money.of("800.00", currency), state.net)
            // 12 elapsed days: the average divides the month by the days that happened.
            assertEquals(Money.of("100.00", currency), state.averagePerDay)
            assertEquals(0.2f, state.changeVsPreviousMonth!!, 0.0001f)
        }

    @Test
    fun `a category without a category id is the uncategorised bucket`() = runTest(testDispatcher) {
        stubSettings()
        every { repository.observeMonthlyReport(october, currency) } returns flowOf(
            report(
                categories = listOf(
                    CategoryReportRow(
                        CategoryId(7),
                        "Groceries",
                        "groceries",
                        0xFF112233,
                        Money.of("900.00", currency),
                        4,
                        0.75f,
                    ),
                    CategoryReportRow(null, null, null, null, Money.of("300.00", currency), 1, 0.25f),
                ),
            ),
        )

        val categories = viewModel().state.value.categories

        assertEquals(listOf(7L, null), categories.map { it.categoryId })
        assertEquals("Groceries", categories.first().name)
        // The screen labels the bucket itself; the state only says that it has no name.
        assertNull(categories.last().name)
        assertEquals(4, categories.first().transactionCount)
    }

    @Test
    fun `walking back asks for the previous month`() = runTest(testDispatcher) {
        stubSettings()
        every { repository.observeMonthlyReport(october, currency) } returns flowOf(report())
        every { repository.observeMonthlyReport(september, currency) } returns flowOf(
            report(
                month = september,
                daysElapsed = 30,
            ),
        )

        val viewModel = viewModel()
        viewModel.onAction(ReportsAction.PreviousMonthClicked)

        verify { repository.observeMonthlyReport(september, currency) }
        assertEquals(september, viewModel.state.value.month)
        assertTrue(viewModel.state.value.canGoNext)
        assertFalse(viewModel.state.value.isCurrentMonth)
    }

    @Test
    fun `the future is not reachable, but the current month is reachable again`() =
        runTest(testDispatcher) {
            stubSettings()
            every { repository.observeMonthlyReport(any(), any()) } answers {
                flowOf(report(month = firstArg(), daysElapsed = 1))
            }
            val viewModel = viewModel()

            viewModel.onAction(ReportsAction.NextMonthClicked)
            assertEquals("the current month may not be left to the right", october, viewModel.state.value.month)

            viewModel.onAction(ReportsAction.PreviousMonthClicked)
            assertEquals(september, viewModel.state.value.month)

            viewModel.onAction(ReportsAction.NextMonthClicked)
            assertEquals(october, viewModel.state.value.month)
            verify(exactly = 0) { repository.observeMonthlyReport(ReportMonth(2026, 11), any()) }
        }

    @Test
    fun `a broken read becomes an error state with a message instead of a spinner`() =
        runTest(testDispatcher) {
            stubSettings()
            every { repository.observeMonthlyReport(any(), any()) } returns flow { throw IOException("disk gone") }
            val viewModel = viewModel()

            assertFalse(viewModel.state.value.isLoading)
            assertTrue(viewModel.state.value.hasFailed)
            assertEquals(
                ReportsEvent.ShowMessage(UiMessage.Resource(R.string.reports_error_load)),
                viewModel.events.first(),
            )
        }

    @Test
    fun `retrying subscribes again and clears the error`() = runTest(testDispatcher) {
        stubSettings()
        every { repository.observeMonthlyReport(october, currency) } returnsMany listOf(
            flow { throw IOException("disk gone") },
            flowOf(report()),
        )
        val viewModel = viewModel()
        assertTrue(viewModel.state.value.hasFailed)

        viewModel.onAction(ReportsAction.RetryClicked)

        assertFalse(viewModel.state.value.hasFailed)
        assertFalse(viewModel.state.value.isLoading)
        assertEquals(Money.of("1200.00", currency), viewModel.state.value.expense)
        verify(exactly = 2) { repository.observeMonthlyReport(october, currency) }
    }

    @Test
    fun `a month without movement is the empty state, not a zero-filled summary`() =
        runTest(testDispatcher) {
            stubSettings()
            every { repository.observeMonthlyReport(october, currency) } returns flowOf(
                report(income = "0.00", expense = "0.00"),
            )

            val state = viewModel().state.value

            assertTrue(state.isEmpty)
            assertFalse(state.isLoading)
        }
}
