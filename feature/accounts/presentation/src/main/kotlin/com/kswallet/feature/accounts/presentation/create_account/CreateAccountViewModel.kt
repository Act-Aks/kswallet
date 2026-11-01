package com.kswallet.feature.accounts.presentation.create_account

import androidx.lifecycle.viewModelScope
import com.kswallet.core.money.CurrencyCode
import com.kswallet.core.money.CurrencyRegistry
import com.kswallet.core.screen.ScreenViewModel
import com.kswallet.feature.accounts.domain.model.AccountName
import com.kswallet.feature.accounts.domain.model.AccountNameResult
import com.kswallet.feature.accounts.domain.model.NewAccount
import com.kswallet.feature.accounts.domain.repository.AccountRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class CreateAccountViewModel(
    private val accountRepository: AccountRepository,
    private val currencyRegistry: CurrencyRegistry,
) : ScreenViewModel<CreateAccountState, CreateAccountAction, CreateAccountEvent>(
    initialState = CreateAccountState(
        selectedCurrencyCode = currencyRegistry.supportedCurrencies().firstOrNull()?.code?.value,
    ),
) {

    override fun onAction(action: CreateAccountAction) {
        when (action) {
            is CreateAccountAction.NameChanged -> updateName(action.value)

            is CreateAccountAction.CurrencySelected -> selectCurrency(action.code)

            CreateAccountAction.SubmitClicked -> submit()
        }
    }

    private fun updateName(value: String) {
        updateState { current ->
            current.copy(
                name = value,
                nameError = null,
                submissionError = null,
            )
        }
    }

    private fun selectCurrency(code: String) {
        updateState { current ->
            current.copy(
                selectedCurrencyCode = code,
                currencyError = null,
                submissionError = null,
            )
        }
    }

    private fun submit() {
        val state = state.value

        val name = when (val result = AccountName.parse(state.name)) {
            is AccountNameResult.Valid -> result.name

            is AccountNameResult.Invalid -> {
                updateState { current ->
                    current.copy(
                        nameError = "Enter a valid account name.",
                    )
                }
                return
            }
        }

        val selectedCode = state.selectedCurrencyCode

        if (selectedCode == null) {
            updateState { current ->
                current.copy(
                    currencyError = "Choose a currency.",
                )
            }
            return
        }

        val currency = currencyRegistry.find(
            CurrencyCode.of(selectedCode),
        )

        if (currency == null) {
            updateState { current ->
                current.copy(
                    currencyError = "Choose a supported currency.",
                )
            }
            return
        }

        updateState { current ->
            current.copy(
                isSubmitting = true,
                submissionError = null,
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
                        isSubmitting = false,
                    )
                }

                sendEvent(CreateAccountEvent.AccountCreated)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                updateState { current ->
                    current.copy(
                        isSubmitting = false,
                        submissionError = "Could not create the account.",
                    )
                }
            }
        }
    }
}