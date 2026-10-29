package com.kswallet.feature.accounts.presentation

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
            AccountsAction.RetryClicked -> retry()
        }
    }

    private fun retry() {
        observationJob?.cancel()

        updateState { current ->
            current.copy(
                content = AccountsContent.Loading,
            )
        }

        observeAccounts()
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
                    val content = accounts.toContent()

                    updateState { current ->
                        current.copy(content = content)
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
}

private fun List<Account>.toContent(): AccountsContent =
    if (isEmpty()) {
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