package com.kswallet.feature.accounts.presentation.edit_account

import androidx.lifecycle.viewModelScope
import com.kswallet.core.screen.ScreenViewModel
import com.kswallet.feature.accounts.domain.model.AccountId
import com.kswallet.feature.accounts.domain.model.AccountName
import com.kswallet.feature.accounts.domain.model.AccountNameResult
import com.kswallet.feature.accounts.domain.model.AccountStatus
import com.kswallet.feature.accounts.domain.repository.AccountRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class EditAccountViewModel(
    private val accountRepository: AccountRepository,
    private val accountId: AccountId,
) : ScreenViewModel<EditAccountState, EditAccountAction, EditAccountEvent>(
    initialState = EditAccountState(),
) {

    init {
        loadAccount()
    }

    override fun onAction(action: EditAccountAction) {
        when (action) {
            is EditAccountAction.NameChanged -> updateName(action.value)

            EditAccountAction.SaveClicked -> save()

            EditAccountAction.ArchiveToggleClicked -> toggleArchive()
        }
    }

    private fun loadAccount() {
        viewModelScope.launch {
            try {
                val account = accountRepository.findById(accountId)

                if (account == null) {
                    updateState { current ->
                        current.copy(
                            isLoading = false,
                            actionError = "Account not found.",
                        )
                    }
                    return@launch
                }

                updateState {
                    EditAccountState(
                        isLoading = false,
                        accountId = account.id.toString(),
                        name = account.name.value,
                        currencyCode = account.currency.code.value,
                        isArchived = account.isArchived,
                    )
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                updateState { current ->
                    current.copy(
                        isLoading = false,
                        actionError = "Could not load the account.",
                    )
                }
            }
        }
    }

    private fun updateName(value: String) {
        updateState { current ->
            current.copy(
                name = value,
                nameError = null,
                actionError = null,
            )
        }
    }

    private fun save() {
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

        updateState { current ->
            current.copy(
                isSaving = true,
                actionError = null,
            )
        }

        viewModelScope.launch {
            try {
                accountRepository.rename(
                    id = accountId,
                    name = name,
                )

                updateState { current ->
                    current.copy(
                        isSaving = false,
                    )
                }

                sendEvent(EditAccountEvent.AccountUpdated)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                updateState { current ->
                    current.copy(
                        isSaving = false,
                        actionError = "Could not rename the account.",
                    )
                }
            }
        }
    }

    private fun toggleArchive() {
        val state = state.value

        val nextStatus = if (state.isArchived) {
            AccountStatus.Active
        } else {
            AccountStatus.Archived
        }

        viewModelScope.launch {
            try {
                accountRepository.setStatus(
                    id = accountId,
                    status = nextStatus,
                )

                updateState { current ->
                    current.copy(
                        isArchived = nextStatus == AccountStatus.Archived,
                        actionError = null,
                    )
                }

                sendEvent(EditAccountEvent.AccountUpdated)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                updateState { current ->
                    current.copy(
                        actionError = "Could not update the account.",
                    )
                }
            }
        }
    }
}