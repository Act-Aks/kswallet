package com.kswallet.feature.accounts.presentation

import androidx.lifecycle.ViewModelStore
import com.kswallet.core.money.CurrencyCode
import com.kswallet.core.money.CurrencyUnit
import com.kswallet.feature.accounts.domain.model.Account
import com.kswallet.feature.accounts.domain.model.AccountId
import com.kswallet.feature.accounts.domain.model.AccountName
import com.kswallet.feature.accounts.domain.model.AccountNameResult
import com.kswallet.feature.accounts.domain.model.AccountStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AccountsViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var repository: FakeAccountRepository
    private lateinit var viewModelStore: ViewModelStore

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        repository = FakeAccountRepository()
        viewModelStore = ViewModelStore()
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
    fun `initial state is loading`() = runTest {
        val viewModel = createViewModel()

        assertEquals(
            AccountsContent.Loading,
            viewModel.state.value.content,
        )

        assertEquals(
            0,
            repository.observationStarts,
        )
    }

    @Test
    fun `empty repository produces empty state`() = runTest {
        val viewModel = createViewModel()

        runCurrent()

        assertEquals(
            AccountsContent.Empty,
            viewModel.state.value.content,
        )

        assertEquals(
            1,
            repository.activeObservers,
        )
    }

    @Test
    fun `accounts map to content preserving order and archive status`() =
        runTest {
            val archived = account(
                name = "Archived wallet",
                status = AccountStatus.Archived,
            )

            val active = account(
                name = "Active wallet",
                status = AccountStatus.Active,
            )

            repository.accounts.value = listOf(
                archived,
                active,
            )

            val viewModel = createViewModel()

            runCurrent()

            assertEquals(
                AccountsContent.Content(
                    accounts = listOf(
                        AccountListItem(
                            id = archived.id.toString(),
                            name = "Archived wallet",
                            currencyCode = "INR",
                            isArchived = true,
                        ),
                        AccountListItem(
                            id = active.id.toString(),
                            name = "Active wallet",
                            currencyCode = "INR",
                            isArchived = false,
                        ),
                    ),
                ),
                viewModel.state.value.content,
            )
        }

    @Test
    fun `subsequent emissions update screen state`() = runTest {
        val viewModel = createViewModel()

        runCurrent()

        assertEquals(
            AccountsContent.Empty,
            viewModel.state.value.content,
        )

        repository.accounts.value = listOf(account())

        runCurrent()

        val content = viewModel.state.value.content

        assertEquals(
            true,
            content is AccountsContent.Content,
        )

        repository.accounts.value = emptyList()

        runCurrent()

        assertEquals(
            AccountsContent.Empty,
            viewModel.state.value.content,
        )
    }

    @Test
    fun `observation failure produces error state`() = runTest {
        repository.nextObservationFailure =
            IllegalStateException("Storage unavailable")

        val viewModel = createViewModel()

        runCurrent()

        assertEquals(
            AccountsContent.Error,
            viewModel.state.value.content,
        )

        assertEquals(
            0,
            repository.activeObservers,
        )
    }

    @Test
    fun `retry restarts failed observation once`() = runTest {
        repository.nextObservationFailure =
            IllegalStateException("Storage unavailable")

        val viewModel = createViewModel()

        runCurrent()

        assertEquals(
            AccountsContent.Error,
            viewModel.state.value.content,
        )

        viewModel.onAction(AccountsAction.RetryClicked)

        assertEquals(
            AccountsContent.Loading,
            viewModel.state.value.content,
        )

        viewModel.onAction(AccountsAction.RetryClicked)

        runCurrent()

        assertEquals(
            AccountsContent.Empty,
            viewModel.state.value.content,
        )

        assertEquals(
            2,
            repository.observationStarts,
        )

        assertEquals(
            1,
            repository.activeObservers,
        )
    }

    @Test
    fun `retry outside error does not restart observation`() = runTest {
        val viewModel = createViewModel()

        viewModel.onAction(AccountsAction.RetryClicked)

        runCurrent()

        viewModel.onAction(AccountsAction.RetryClicked)

        runCurrent()

        assertEquals(
            1,
            repository.observationStarts,
        )

        assertEquals(
            1,
            repository.activeObservers,
        )
    }

    @Test
    fun `cancellation is not converted into error state`() = runTest {
        repository.nextObservationFailure =
            CancellationException("Observation cancelled")

        val viewModel = createViewModel()

        runCurrent()

        assertEquals(
            AccountsContent.Loading,
            viewModel.state.value.content,
        )

        assertEquals(
            0,
            repository.activeObservers,
        )
    }

    @Test
    fun `clearing view model cancels observation`() = runTest {
        createViewModel()

        runCurrent()

        assertEquals(
            1,
            repository.activeObservers,
        )

        viewModelStore.clear()

        runCurrent()

        assertEquals(
            0,
            repository.activeObservers,
        )
    }

    private fun createViewModel(): AccountsViewModel {
        val viewModel = AccountsViewModel(
            accountRepository = repository,
        )

        viewModelStore.put(
            "accounts",
            viewModel,
        )

        return viewModel
    }

    private fun account(
        name: String = "Travel",
        status: AccountStatus = AccountStatus.Active,
    ): Account =
        Account(
            id = AccountId.new(),
            name = validName(name),
            currency = CurrencyUnit(
                code = CurrencyCode.of("INR"),
                minorUnitScale = 2,
            ),
            status = status,
        )

    private fun validName(value: String): AccountName =
        when (val result = AccountName.parse(value)) {
            is AccountNameResult.Valid -> result.name

            is AccountNameResult.Invalid -> {
                error("Invalid test name: ${result.reason}")
            }
        }
}