package com.kswallet.feature.accounts.presentation.accounts

import androidx.lifecycle.viewModelScope
import com.kswallet.core.screen.ScreenViewModel
import com.kswallet.feature.accounts.domain.model.Account
import com.kswallet.feature.accounts.domain.repository.AccountRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AccountsViewModel(
    private val accountRepository: AccountRepository,
) : ScreenViewModel<AccountsState, AccountsAction, AccountsEvent>(
    initialState = AccountsState(),
) {

    private var observationJob: Job? = null

    init {
        observeAccounts()
    }

    override fun onAction(action: AccountsAction) {
        when (action) {
            AccountsAction.RetryClicked -> observeAccounts()
            AccountsAction.CreateAccountClicked -> openCreateAccount()
            is AccountsAction.AccountClicked -> openAccountDetails(action.id)
        }
    }

    private fun observeAccounts() {
        observationJob?.cancel()

        updateState { current ->
            current.copy(
                content = AccountsContent.Loading,
            )
        }

        observationJob = viewModelScope.launch {
            try {
                accountRepository.observeAccounts().collect { accounts ->
                    updateState { current ->
                        current.copy(
                            content = accounts.toContent(),
                        )
                    }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                updateState { current ->
                    current.copy(
                        content = AccountsContent.Error,
                    )
                }
            }
        }
    }

    private fun List<Account>.toContent(): AccountsContent = if (isEmpty()) {
        AccountsContent.Empty
    } else {
        AccountsContent.Content(
            accounts = map { account ->
                AccountListItem(
                    id = account.id.toString(),
                    name = account.name.value,
                    currencyCode = account.currency.code.value,
                    isArchived = account.isArchived,
                )
            },
        )
    }

    private fun openAccountDetails(id: String) {
        viewModelScope.launch {
            sendEvent(AccountsEvent.OpenAccountDetails(id))
        }
    }

    private fun openCreateAccount() {
        viewModelScope.launch {
            sendEvent(AccountsEvent.OpenCreateAccount)
        }
    }
}