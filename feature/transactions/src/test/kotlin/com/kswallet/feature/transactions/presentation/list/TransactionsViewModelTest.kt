package com.kswallet.feature.transactions.presentation.list

import com.kswallet.core.common.result.Outcome
import com.kswallet.core.datastore.domain.model.UserSettings
import com.kswallet.core.datastore.domain.repository.SettingsRepository
import com.kswallet.core.model.AccountId
import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import com.kswallet.core.model.Transaction
import com.kswallet.core.model.TransactionId
import com.kswallet.core.model.TransactionType
import com.kswallet.core.testing.FixedTimeProvider
import com.kswallet.core.testing.MainDispatcherRule
import com.kswallet.feature.transactions.domain.model.CashFlow
import com.kswallet.feature.transactions.domain.model.PeriodOption
import com.kswallet.feature.transactions.domain.model.TransactionDetails
import com.kswallet.feature.transactions.domain.repository.TransactionRepository
import com.kswallet.feature.transactions.domain.usecase.DeleteTransactionUseCase
import com.kswallet.feature.transactions.domain.usecase.ObserveCashFlowUseCase
import com.kswallet.feature.transactions.domain.usecase.ObserveTransactionsUseCase
import com.kswallet.feature.transactions.domain.usecase.ResolvePeriodUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * The ledger ViewModel: filter -> re-query, row -> editor, delete -> repository.
 *
 * The list itself is the mapper's job (tested separately), so this test only pins the
 * wiring between actions, the data streams and the effects.
 */
@OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class)
class TransactionsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private val zone = TimeZone.of("Asia/Kolkata")
    private val timeProvider = FixedTimeProvider.of("2026-10-12", zone)
    private val currency = Currency.INR

    private val repository = mockk<TransactionRepository>()
    private val settingsRepository = mockk<SettingsRepository>()

    private fun details(
        id: Long,
        type: TransactionType,
        amount: String,
        date: String = "2026-10-12",
        category: String? = "Groceries",
    ) = TransactionDetails(
        transaction = Transaction(
            id = TransactionId(id),
            type = type,
            amount = Money.of(amount, currency),
            accountId = AccountId(1),
            categoryId = category?.let { CategoryId(10) },
            dateTime = Instant.parse("${date}T06:30:00Z"),
            note = null,
            createdAt = Instant.parse("${date}T06:30:00Z"),
        ),
        accountName = "Cash",
        transferAccountName = null,
        categoryName = category,
        categoryIconKey = "cart",
        categoryColorArgb = 0xFF112233,
    )

    private fun viewModel() = TransactionsViewModel(
        observeTransactions = ObserveTransactionsUseCase(repository, ResolvePeriodUseCase(timeProvider)),
        observeCashFlow = ObserveCashFlowUseCase(repository, ResolvePeriodUseCase(timeProvider)),
        deleteTransaction = DeleteTransactionUseCase(repository),
        settingsRepository = settingsRepository,
        timeProvider = timeProvider,
    )

    private fun stubHappyPath() {
        every { settingsRepository.observeSettings() } returns flowOf(UserSettings(defaultCurrency = currency))
        every { repository.observeTransactions(any(), any(), any(), any(), any()) } returns flowOf(
            listOf(
                details(1, TransactionType.EXPENSE, "1200.00"),
                details(2, TransactionType.INCOME, "3000.00"),
            ),
        )
        every { repository.observeCashFlow(any(), currency) } returns flowOf(
            CashFlow(
                income = Money.of("3000.00", currency),
                expense = Money.of("1200.00", currency),
            ),
        )
    }

    @Test
    fun `renders today's entries with the totals of the selected period`() = runTest(testDispatcher) {
        stubHappyPath()

        val viewModel = viewModel()

        assertFalse(viewModel.state.value.isLoading)
        assertEquals(1, viewModel.state.value.dayGroups.size)
        assertEquals("Today", viewModel.state.value.dayGroups.first().header)
        assertEquals(2, viewModel.state.value.dayGroups.first().items.size)
        assertEquals(Money.of("3000.00", currency), viewModel.state.value.periodIncome)
        assertEquals(Money.of("1200.00", currency), viewModel.state.value.periodExpense)
    }

    @Test
    fun `an empty ledger is an empty state, not a loading state`() = runTest(testDispatcher) {
        every { settingsRepository.observeSettings() } returns flowOf(UserSettings(defaultCurrency = currency))
        every { repository.observeTransactions(any(), any(), any(), any(), any()) } returns flowOf(emptyList())
        every { repository.observeCashFlow(any(), currency) } returns flowOf(
            CashFlow(
                Money.zero(currency),
                Money.zero(currency),
            ),
        )

        val viewModel = viewModel()

        assertFalse(viewModel.state.value.isLoading)
        assertTrue(viewModel.state.value.isEmpty)
    }

    @Test
    fun `changing the type filter re-queries the ledger with it`() = runTest(testDispatcher) {
        stubHappyPath()
        val viewModel = viewModel()

        viewModel.onAction(TransactionsAction.TypeFilterSelected(TransactionType.EXPENSE))

        assertEquals(TransactionType.EXPENSE, viewModel.state.value.filter.type)
        coVerify {
            repository.observeTransactions(any(), TransactionType.EXPENSE, any(), any(), any())
        }
    }

    @Test
    fun `changing the period re-queries the ledger over the new range`() = runTest(testDispatcher) {
        stubHappyPath()
        val viewModel = viewModel()

        viewModel.onAction(TransactionsAction.PeriodSelected(PeriodOption.ALL_TIME))

        assertEquals(PeriodOption.ALL_TIME, viewModel.state.value.filter.period)
        coVerify { repository.observeCashFlow(any(), currency) }
    }

    @Test
    fun `tapping an entry asks to open the editor for it`() = runTest(testDispatcher) {
        stubHappyPath()
        val viewModel = viewModel()

        viewModel.onAction(TransactionsAction.TransactionClicked(TransactionId(2)))

        assertEquals(TransactionsEvent.OpenEditor(TransactionId(2)), viewModel.events.first())
    }

    @Test
    fun `adding an entry opens the editor without an id`() = runTest(testDispatcher) {
        stubHappyPath()
        val viewModel = viewModel()

        viewModel.onAction(TransactionsAction.AddTransactionClicked)

        assertEquals(TransactionsEvent.OpenEditor(null), viewModel.events.first())
    }

    @Test
    fun `deleting asks for confirmation first and then deletes`() = runTest(testDispatcher) {
        stubHappyPath()
        coEvery { repository.deleteTransaction(TransactionId(1)) } returns Outcome.Success(Unit)
        val viewModel = viewModel()

        viewModel.onAction(TransactionsAction.DeleteClicked(TransactionId(1)))

        assertEquals(TransactionId(1), viewModel.state.value.pendingDelete?.id)
        coVerify(exactly = 0) { repository.deleteTransaction(any()) }

        viewModel.onAction(TransactionsAction.DeleteConfirmed)

        coVerify { repository.deleteTransaction(TransactionId(1)) }
        assertNull(viewModel.state.value.pendingDelete)
    }

    @Test
    fun `dismissing the confirmation keeps the entry`() = runTest(testDispatcher) {
        stubHappyPath()
        val viewModel = viewModel()

        viewModel.onAction(TransactionsAction.DeleteClicked(TransactionId(1)))
        viewModel.onAction(TransactionsAction.DeleteDismissed)

        assertNull(viewModel.state.value.pendingDelete)
        coVerify(exactly = 0) { repository.deleteTransaction(any()) }
    }
}
