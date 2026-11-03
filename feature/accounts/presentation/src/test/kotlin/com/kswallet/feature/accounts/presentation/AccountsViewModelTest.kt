package com.kswallet.feature.accounts.presentation

import androidx.lifecycle.ViewModelStore
import com.kswallet.core.identity.AccountId
import com.kswallet.core.money.CurrencyCode
import com.kswallet.core.money.CurrencyUnit
import com.kswallet.feature.accounts.domain.model.Account
import com.kswallet.feature.accounts.domain.model.AccountName
import com.kswallet.feature.accounts.domain.model.AccountNameResult
import com.kswallet.feature.accounts.domain.model.AccountStatus
import com.kswallet.feature.accounts.presentation.accounts.AccountListItem
import com.kswallet.feature.accounts.presentation.accounts.AccountsAction
import com.kswallet.feature.accounts.presentation.accounts.AccountsContent
import com.kswallet.feature.accounts.presentation.accounts.AccountsViewModel
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

    private lateinit var repository: FakeAccountRepositoryImpl
    private lateinit var viewModelStore: ViewModelStore
    private lateinit var viewModel: AccountsViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        repository = FakeAccountRepositoryImpl()
        viewModelStore = ViewModelStore()

        viewModel = AccountsViewModel(
            accountRepository = repository,
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
            )

            viewModelStore.put(
                AccountsViewModel::class.java,
                delayedViewModel,
            )

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
    fun `accounts map to list items preserving order and archive status`() =
        runTest(dispatcher) {
            runCurrent()

            val archived = account(
                name = "Archived wallet",
                isArchived = true,
            )

            val active = account(
                name = "Active wallet",
                isArchived = false,
            )

            repository.accounts.value = listOf(archived, active)

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
    fun `retry restarts observation`() = runTest(dispatcher) {
        runCurrent()

        viewModel.onAction(AccountsAction.RetryClicked)
        runCurrent()

        assertEquals(2, repository.observationStarts)
        assertEquals(1, repository.activeObservers)
    }

    private fun account(
        name: String,
        isArchived: Boolean,
    ): Account = Account(
        id = AccountId.new(),
        name = validName(name),
        currency = CurrencyUnit(
            code = CurrencyCode.of("INR"),
            minorUnitScale = 2,
        ),
        status = if (isArchived) {
            AccountStatus.Archived
        } else {
            AccountStatus.Active
        },
    )

    private fun validName(value: String): AccountName =
        when (val result = AccountName.parse(value)) {
            is AccountNameResult.Valid -> result.name

            is AccountNameResult.Invalid ->
                error("Invalid test account name: ${result.reason}")
        }
}