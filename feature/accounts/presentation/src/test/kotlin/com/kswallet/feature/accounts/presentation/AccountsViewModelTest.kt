package com.kswallet.feature.accounts.presentation

import androidx.lifecycle.ViewModelStore
import com.kswallet.core.money.CurrencyCode
import com.kswallet.core.money.CurrencyRegistry
import com.kswallet.core.money.CurrencyUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AccountsViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var repository: FakeAccountRepository
    private lateinit var viewModelStore: ViewModelStore
    private lateinit var viewModel: AccountsViewModel

    private val currencyRegistry = FakeCurrencyRegistry(
        listOf(
            currency("INR", scale = 2),
            currency("USD", scale = 2),
        ),
    )

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        repository = FakeAccountRepository()
        viewModelStore = ViewModelStore()

        viewModel = AccountsViewModel(
            accountRepository = repository,
            currencyRegistry = currencyRegistry,
        )

        viewModelStore.put(AccountsViewModel::class.java, viewModel)
    }

    @AfterEach
    fun tearDown() {
        try {
            viewModelStore.clear()
            dispatcher.scheduler.runCurrent()
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `observation starts when view model is created`() =
        runTest(dispatcher) {
            runCurrent()

            assertEquals(1, repository.observationStarts)
            assertEquals(1, repository.activeObservers)
        }

    @Test
    fun `empty repository produces empty state`() = runTest(dispatcher) {
        runCurrent()

        assertEquals(
            AccountsContent.Empty,
            viewModel.state.value.content,
        )
    }

    @Test
    fun `state remains loading until repository emits`() =
        runTest(dispatcher) {
            repository.emitAfterDelay = true

            val delayedViewModel = AccountsViewModel(
                accountRepository = repository,
                currencyRegistry = currencyRegistry,
            )

            viewModelStore.put(AccountsViewModel::class.java, delayedViewModel)

            assertEquals(
                AccountsContent.Loading,
                delayedViewModel.state.value.content,
            )

            repository.releaseEmission()
            runCurrent()

            assertEquals(
                AccountsContent.Empty,
                delayedViewModel.state.value.content,
            )
        }

    @Test
    fun `show create account opens form with default currency`() =
        runTest(dispatcher) {
            runCurrent()

            viewModel.onAction(AccountsAction.ShowCreateAccountClicked)

            val form = viewModel.state.value.createAccount

            assertTrue(form.isVisible)
            assertEquals("", form.name)
            assertEquals("INR", form.selectedCurrencyCode)
            assertFalse(form.canSubmit)
        }

    @Test
    fun `dismiss create account resets the form`() = runTest(dispatcher) {
        runCurrent()

        viewModel.onAction(AccountsAction.ShowCreateAccountClicked)
        viewModel.onAction(AccountsAction.NameChanged("Travel"))
        viewModel.onAction(AccountsAction.CurrencySelected("INR"))

        viewModel.onAction(AccountsAction.DismissCreateAccountClicked)

        val form = viewModel.state.value.createAccount

        assertFalse(form.isVisible)
        assertEquals("", form.name)
        assertNull(form.selectedCurrencyCode)
    }

    @Test
    fun `invalid name prevents submission`() = runTest(dispatcher) {
        runCurrent()

        viewModel.onAction(AccountsAction.ShowCreateAccountClicked)
        viewModel.onAction(AccountsAction.NameChanged(""))
        viewModel.onAction(AccountsAction.CurrencySelected("INR"))

        viewModel.onAction(AccountsAction.SubmitCreateAccountClicked)
        runCurrent()

        val form = viewModel.state.value.createAccount

        assertEquals("Enter a valid account name.", form.nameError)
        assertFalse(form.isSubmitting)
        assertEquals(0, repository.accounts.value.size)
    }

    @Test
    fun `default currency is the first supported currency`() =
        runTest(dispatcher) {
            runCurrent()

            viewModel.onAction(AccountsAction.ShowCreateAccountClicked)

            assertEquals(
                "INR",
                viewModel.state.value.createAccount.selectedCurrencyCode,
            )
        }
    
    @Test
    fun `valid submission creates an account and emits event`() =
        runTest(dispatcher) {
            runCurrent()

            val event = async {
                viewModel.events.first()
            }

            viewModel.onAction(AccountsAction.ShowCreateAccountClicked)
            viewModel.onAction(AccountsAction.NameChanged("Travel"))
            viewModel.onAction(AccountsAction.CurrencySelected("INR"))

            viewModel.onAction(AccountsAction.SubmitCreateAccountClicked)
            advanceUntilIdle()

            val form = viewModel.state.value.createAccount

            assertFalse(form.isVisible)
            assertEquals("", form.name)
            assertNull(form.selectedCurrencyCode)

            assertEquals(1, repository.accounts.value.size)
            assertEquals(
                "Travel",
                repository.accounts.value.first().name.value,
            )
            assertEquals(
                "INR",
                repository.accounts.value.first().currency.code.value,
            )

            assertEquals(
                AccountsEvent.AccountCreated,
                event.await(),
            )
        }

    @Test
    fun `repository failure shows submission error and retains form`() =
        runTest(dispatcher) {
            runCurrent()

            repository.nextCreateFailure =
                IllegalStateException("Storage unavailable")

            viewModel.onAction(AccountsAction.ShowCreateAccountClicked)
            viewModel.onAction(AccountsAction.NameChanged("Travel"))
            viewModel.onAction(AccountsAction.CurrencySelected("INR"))

            viewModel.onAction(AccountsAction.SubmitCreateAccountClicked)
            advanceUntilIdle()

            val form = viewModel.state.value.createAccount

            assertTrue(form.isVisible)
            assertFalse(form.isSubmitting)
            assertEquals(
                "Could not create the account.",
                form.submissionError,
            )
            assertEquals(0, repository.accounts.value.size)
        }

    private fun currency(
        code: String,
        scale: Int,
    ): CurrencyUnit = CurrencyUnit(
        code = CurrencyCode.of(code),
        minorUnitScale = scale,
    )

    private class FakeCurrencyRegistry(
        private val currencies: List<CurrencyUnit>,
    ) : CurrencyRegistry {

        private val currenciesByCode = currencies.associateBy { currency ->
            currency.code
        }

        override fun find(code: CurrencyCode): CurrencyUnit? =
            currenciesByCode[code]

        override fun supportedCurrencies(): List<CurrencyUnit> =
            currencies.sortedBy { currency ->
                currency.code.value
            }
    }
}