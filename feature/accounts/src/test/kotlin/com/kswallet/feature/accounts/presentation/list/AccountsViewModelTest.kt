package com.kswallet.feature.accounts.presentation.list

import com.kswallet.core.common.result.Outcome
import com.kswallet.core.datastore.domain.model.UserSettings
import com.kswallet.core.datastore.domain.repository.SettingsRepository
import com.kswallet.core.model.Account
import com.kswallet.core.model.AccountId
import com.kswallet.core.model.AccountType
import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import com.kswallet.core.testing.MainDispatcherRule
import com.kswallet.feature.accounts.domain.repository.AccountRepository
import com.kswallet.feature.accounts.domain.usecase.DeleteAccountUseCase
import com.kswallet.feature.accounts.domain.usecase.GetAccountTransactionCountUseCase
import com.kswallet.feature.accounts.domain.usecase.ObserveAccountsUseCase
import com.kswallet.feature.accounts.domain.usecase.ObserveForeignCurrencyAccountsUseCase
import com.kswallet.feature.accounts.domain.usecase.ObserveTotalBalanceUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
class AccountsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private val repository = mockk<AccountRepository>()
    private val settingsRepository = mockk<SettingsRepository>()

    private val savings = Account(
        id = AccountId(1),
        name = "HDFC Savings",
        type = AccountType.BANK,
        currency = Currency.INR,
        balance = Money.of("2500.00", Currency.INR),
        initialBalance = Money.of("1000.00", Currency.INR),
        iconKey = "bank",
        colorArgb = 0xFF0E7C66,
        includedInTotal = true,
        createdAt = Instant.fromEpochMilliseconds(0),
    )

    private fun viewModel() = AccountsViewModel(
        observeAccounts = ObserveAccountsUseCase(repository),
        observeTotalBalance = ObserveTotalBalanceUseCase(repository),
        observeForeignCurrencies = ObserveForeignCurrencyAccountsUseCase(repository),
        getTransactionCount = GetAccountTransactionCountUseCase(repository),
        deleteAccount = DeleteAccountUseCase(repository),
        settingsRepository = settingsRepository,
    )

    private fun stubHappyPath() {
        every { settingsRepository.observeSettings() } returns flowOf(UserSettings(defaultCurrency = Currency.INR))
        every { repository.observeAccounts() } returns flowOf(listOf(savings))
        every { repository.observeTotalBalance(Currency.INR) } returns flowOf(Money.of("2500.00", Currency.INR))
        every { repository.observeForeignCurrencyAccountCount(Currency.INR) } returns flowOf(1)
    }

    @Test
    fun `renders accounts, the total of the base currency and the hidden foreign ones`() =
        runTest(testDispatcher) {
            stubHappyPath()

            val viewModel = viewModel()

            assertFalse(viewModel.state.value.isLoading)
            assertEquals(1, viewModel.state.value.accounts.size)
            assertEquals("HDFC Savings", viewModel.state.value.accounts.first().name)
            assertEquals(Money.of("2500.00", Currency.INR), viewModel.state.value.totalBalance)
            assertEquals(1, viewModel.state.value.hiddenForeignCurrencyAccounts)
        }

    @Test
    fun `an empty database is an empty state, not a loading state`() = runTest(testDispatcher) {
        every { settingsRepository.observeSettings() } returns flowOf(UserSettings())
        every { repository.observeAccounts() } returns flowOf(emptyList())
        every { repository.observeTotalBalance(any()) } returns flowOf(Money.zero(Currency.INR))
        every { repository.observeForeignCurrencyAccountCount(any()) } returns flowOf(0)

        val viewModel = viewModel()

        assertFalse(viewModel.state.value.isLoading)
        assertEquals(true, viewModel.state.value.isEmpty)
    }

    @Test
    fun `deleting warns about the transactions that will go with the account`() =
        runTest(testDispatcher) {
            stubHappyPath()
            coEvery { repository.countTransactions(AccountId(1)) } returns Outcome.Success(5)

            val viewModel = viewModel()
            viewModel.onAction(AccountsAction.DeleteClicked(AccountId(1)))

            val pending = viewModel.state.value.pendingDelete
            assertEquals(AccountId(1), pending?.id)
            assertEquals(5, pending?.transactionCount)
        }

    @Test
    fun `confirming the dialog deletes with force and clears the dialog`() = runTest(testDispatcher) {
        stubHappyPath()
        coEvery { repository.countTransactions(AccountId(1)) } returns Outcome.Success(5)
        coEvery { repository.deleteAccount(AccountId(1)) } returns Outcome.Success(Unit)

        val viewModel = viewModel()
        viewModel.onAction(AccountsAction.DeleteClicked(AccountId(1)))
        viewModel.onAction(AccountsAction.DeleteConfirmed)

        coVerify { repository.deleteAccount(AccountId(1)) }
        assertNull(viewModel.state.value.pendingDelete)
    }

    @Test
    fun `add account opens the editor with no id`() = runTest(testDispatcher) {
        stubHappyPath()
        val viewModel = viewModel()

        viewModel.onAction(AccountsAction.AddAccountClicked)

        assertEquals(
            AccountsEvent.OpenEditor(null),
            viewModel.events.first(),
        )
    }
}
