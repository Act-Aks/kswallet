package com.kswallet.feature.accounts.presentation.create_account

import androidx.lifecycle.ViewModelStore
import com.kswallet.core.money.CurrencyCode
import com.kswallet.core.money.CurrencyRegistry
import com.kswallet.core.money.CurrencyUnit
import com.kswallet.feature.accounts.presentation.FakeAccountRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateAccountViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var repository: FakeAccountRepositoryImpl
    private lateinit var viewModelStore: ViewModelStore
    private lateinit var viewModel: CreateAccountViewModel

    private val currencyRegistry = FakeCurrencyRegistry(
        listOf(
            currency("INR", scale = 2),
            currency("USD", scale = 2),
        ),
    )

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        repository = FakeAccountRepositoryImpl()
        viewModelStore = ViewModelStore()

        viewModel = CreateAccountViewModel(
            accountRepository = repository,
            currencyRegistry = currencyRegistry,
        )

        viewModelStore.put(CreateAccountViewModel::class.java, viewModel)
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
    fun `default currency is the first supported currency`() =
        runTest(dispatcher) {
            assertEquals(
                "INR",
                viewModel.state.value.selectedCurrencyCode,
            )
        }

    @Test
    fun `valid submission creates an account and emits event`() =
        runTest(dispatcher) {
            viewModel.onAction(
                CreateAccountAction.NameChanged("Travel"),
            )

            viewModel.onAction(
                CreateAccountAction.SubmitClicked,
            )

            advanceUntilIdle()

            assertEquals(1, repository.accounts.value.size)
            assertEquals(
                "Travel",
                repository.accounts.value.first().name.value,
            )
            assertEquals(
                "INR",
                repository.accounts.value.first().currency.code.value,
            )
        }

    @Test
    fun `invalid name prevents submission`() =
        runTest(dispatcher) {
            viewModel.onAction(
                CreateAccountAction.NameChanged(""),
            )

            viewModel.onAction(
                CreateAccountAction.SubmitClicked,
            )

            runCurrent()

            assertEquals(
                "Enter a valid account name.",
                viewModel.state.value.nameError,
            )
            assertEquals(0, repository.accounts.value.size)
        }

    @Test
    fun `repository failure shows submission error`() =
        runTest(dispatcher) {
            repository.nextCreateFailure =
                IllegalStateException("Storage unavailable")

            viewModel.onAction(
                CreateAccountAction.NameChanged("Travel"),
            )

            viewModel.onAction(
                CreateAccountAction.SubmitClicked,
            )

            advanceUntilIdle()

            assertEquals(
                "Could not create the account.",
                viewModel.state.value.submissionError,
            )
            assertFalse(viewModel.state.value.isSubmitting)
            assertEquals(0, repository.accounts.value.size)
        }

    private fun currency(
        code: String,
        scale: Int,
    ): CurrencyUnit = CurrencyUnit(
        code = CurrencyCode.of(code),
        minorUnitScale = scale,
    )
}

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