package com.kswallet.feature.home.presentation.home

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
import com.kswallet.feature.accounts.domain.repository.AccountRepository
import com.kswallet.feature.accounts.domain.usecase.CountAccountsUseCase
import com.kswallet.feature.accounts.domain.usecase.ObserveTotalBalanceUseCase
import com.kswallet.feature.home.domain.usecase.ObserveHomeSummaryUseCase
import com.kswallet.feature.transactions.domain.model.CashFlow
import com.kswallet.feature.transactions.domain.model.TransactionDetails
import com.kswallet.feature.transactions.domain.repository.TransactionRepository
import com.kswallet.feature.transactions.domain.usecase.ObserveCashFlowUseCase
import com.kswallet.feature.transactions.domain.usecase.ObserveRecentTransactionsUseCase
import com.kswallet.feature.transactions.domain.usecase.ResolvePeriodUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private val accountRepository = mockk<AccountRepository>()
    private val transactionRepository = mockk<TransactionRepository>()
    private val settingsRepository = mockk<SettingsRepository>()
    private val timeProvider = FixedTimeProvider.of("2026-10-12")

    private val details = TransactionDetails(
        transaction = Transaction(
            id = TransactionId(1),
            type = TransactionType.EXPENSE,
            amount = Money.of("120.00", Currency.INR),
            accountId = AccountId(1),
            categoryId = CategoryId(2),
            dateTime = Instant.fromEpochMilliseconds(0),
            createdAt = Instant.fromEpochMilliseconds(0),
        ),
        accountName = "Cash",
        transferAccountName = null,
        categoryName = "Groceries",
        categoryIconKey = "groceries",
        categoryColorArgb = 0xFF112233,
    )

    private fun viewModel(): HomeViewModel {
        every { settingsRepository.observeSettings() } returns flowOf(UserSettings(defaultCurrency = Currency.INR))
        every { accountRepository.observeTotalBalance(Currency.INR) } returns flowOf(Money.of("5000.00", Currency.INR))
        coEvery { accountRepository.countAccounts() } returns Outcome.Success(2)
        every { transactionRepository.observeCashFlow(any(), Currency.INR) } returns flowOf(
            CashFlow(
                income = Money.of("3000.00", Currency.INR),
                expense = Money.of("1200.00", Currency.INR),
            ),
        )
        every { transactionRepository.observeRecentTransactions(any()) } returns flowOf(listOf(details))

        return HomeViewModel(
            observeHomeSummary = ObserveHomeSummaryUseCase(
                observeTotalBalance = ObserveTotalBalanceUseCase(accountRepository),
                countAccounts = CountAccountsUseCase(accountRepository),
                observeCashFlow = ObserveCashFlowUseCase(transactionRepository, ResolvePeriodUseCase(timeProvider)),
                observeRecentTransactions = ObserveRecentTransactionsUseCase(transactionRepository),
            ),
            settingsRepository = settingsRepository,
        )
    }

    @Test
    fun `dashboard shows balance, month totals and recent activity`() = runTest(testDispatcher) {
        val viewModel = viewModel()

        assertFalse(viewModel.state.value.isLoading)
        assertEquals(Money.of("5000.00", Currency.INR), viewModel.state.value.totalBalance)
        assertEquals(Money.of("1800.00", Currency.INR), viewModel.state.value.netThisMonth)
        assertEquals(1, viewModel.state.value.recentTransactions.size)
        assertEquals("Groceries", viewModel.state.value.recentTransactions.first().title)
        assertEquals(false, viewModel.state.value.needsFirstAccount)
    }
}
