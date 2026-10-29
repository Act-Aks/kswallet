package com.kswallet.feature.accounts.presentation

import androidx.lifecycle.viewModelScope
import com.kswallet.core.money.CurrencyRegistry
import com.kswallet.core.screen.ScreenViewModel
import com.kswallet.feature.accounts.domain.model.Account
import com.kswallet.feature.accounts.domain.model.AccountName
import com.kswallet.feature.accounts.domain.model.AccountNameResult
import com.kswallet.feature.accounts.domain.model.NewAccount
import com.kswallet.feature.accounts.domain.repository.AccountRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AccountsViewModel(
    private val accountRepository: AccountRepository,
    private val currencyRegistry: CurrencyRegistry,
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

            AccountsAction.ShowCreateAccountClicked -> showCreateAccount()

            AccountsAction.DismissCreateAccountClicked -> dismissCreateAccount()

            is AccountsAction.NameChanged -> updateName(action.value)

            is AccountsAction.CurrencySelected -> selectCurrency(action.code)

            AccountsAction.SubmitCreateAccountClicked -> submitCreateAccount()
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

    private fun showCreateAccount() {
        updateState { current ->
            current.copy(
                createAccount = CreateAccountState(
                    isVisible = true,
                    selectedCurrencyCode = current.createAccount.selectedCurrencyCode,
                ),
            )
        }
    }

    private fun dismissCreateAccount() {
        updateState { current ->
            current.copy(
                createAccount = CreateAccountState(),
            )
        }
    }

    private fun updateName(value: String) {
        updateState { current ->
            current.copy(
                createAccount = current.createAccount.copy(
                    name = value,
                    nameError = null,
                    submissionError = null,
                ),
            )
        }
    }

    private fun selectCurrency(code: String) {
        updateState { current ->
            current.copy(
                createAccount = current.createAccount.copy(
                    selectedCurrencyCode = code,
                    currencyError = null,
                    submissionError = null,
                ),
            )
        }
    }

    private fun submitCreateAccount() {
        val form = state.value.createAccount

        val nameResult = AccountName.parse(form.name)

        val name = when (nameResult) {
            is AccountNameResult.Valid -> nameResult.name

            is AccountNameResult.Invalid -> {
                updateState { current ->
                    current.copy(
                        createAccount = current.createAccount.copy(
                            nameError = "Enter a valid account name.",
                        ),
                    )
                }
                return
            }
        }

        val selectedCode = form.selectedCurrencyCode

        if (selectedCode == null) {
            updateState { current ->
                current.copy(
                    createAccount = current.createAccount.copy(
                        currencyError = "Choose a currency.",
                    ),
                )
            }
            return
        }

        val currency = currencyRegistry.find(
            com.kswallet.core.money.CurrencyCode.of(selectedCode),
        )

        if (currency == null) {
            updateState { current ->
                current.copy(
                    createAccount = current.createAccount.copy(
                        currencyError = "Choose a supported currency.",
                    ),
                )
            }
            return
        }

        updateState { current ->
            current.copy(
                createAccount = current.createAccount.copy(
                    isSubmitting = true,
                    submissionError = null,
                ),
            )
        }

        viewModelScope.launch {
            try {
                accountRepository.create(
                    NewAccount(
                        name = name,
                        currency = currency,
                    ),
                )

                updateState { current ->
                    current.copy(
                        createAccount = CreateAccountState(),
                    )
                }

                sendEvent(AccountsEvent.AccountCreated)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                updateState { current ->
                    current.copy(
                        createAccount = current.createAccount.copy(
                            isSubmitting = false,
                            submissionError = "Could not create the account.",
                        ),
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
}