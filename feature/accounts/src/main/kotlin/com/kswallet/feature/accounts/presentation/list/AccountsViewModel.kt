package com.kswallet.feature.accounts.presentation.list

import androidx.lifecycle.viewModelScope
import com.kswallet.core.common.result.Outcome
import com.kswallet.core.datastore.domain.repository.SettingsRepository
import com.kswallet.core.ui.screen.ScreenViewModel
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.feature.accounts.R
import com.kswallet.feature.accounts.domain.usecase.DeleteAccountUseCase
import com.kswallet.feature.accounts.domain.usecase.GetAccountTransactionCountUseCase
import com.kswallet.feature.accounts.domain.usecase.ObserveAccountsUseCase
import com.kswallet.feature.accounts.domain.usecase.ObserveForeignCurrencyAccountsUseCase
import com.kswallet.feature.accounts.domain.usecase.ObserveTotalBalanceUseCase
import com.kswallet.feature.accounts.presentation.error.toUiMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class AccountsViewModel(
    observeAccounts: ObserveAccountsUseCase,
    observeTotalBalance: ObserveTotalBalanceUseCase,
    observeForeignCurrencies: ObserveForeignCurrencyAccountsUseCase,
    private val getTransactionCount: GetAccountTransactionCountUseCase,
    private val deleteAccount: DeleteAccountUseCase,
    settingsRepository: SettingsRepository,
) : ScreenViewModel<AccountsState, AccountsAction, AccountsEvent>(AccountsState()) {

    init {
        // The base currency comes from settings, so the total follows the user's choice.
        settingsRepository.observeSettings()
            .map { it.defaultCurrency }
            .distinctUntilChanged()
            .flatMapLatest { currency ->
                combine(
                    observeAccounts(),
                    observeTotalBalance(currency),
                    observeForeignCurrencies(currency),
                ) { accounts, total, foreignCount ->
                    AccountsState(
                        isLoading = false,
                        accounts = accounts.map { account ->
                            AccountCardUi(
                                id = account.id,
                                name = account.name,
                                type = account.type,
                                currency = account.currency,
                                balance = account.balance,
                                colorArgb = account.colorArgb,
                                includedInTotal = account.includedInTotal,
                            )
                        },
                        totalBalance = total,
                        hiddenForeignCurrencyAccounts = foreignCount,
                    )
                }
            }
            .onEach { latest -> setState { latest.copy(pendingDelete = currentState.pendingDelete) } }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: AccountsAction) {
        when (action) {
            AccountsAction.AddAccountClicked -> sendEvent(AccountsEvent.OpenEditor(null))

            is AccountsAction.AccountClicked -> sendEvent(AccountsEvent.OpenAccountDetail(action.id))

            is AccountsAction.DeleteClicked -> viewModelScope.launch {
                val card = currentState.accounts.firstOrNull { it.id == action.id } ?: return@launch
                // Ask the database how much history this account carries before offering to delete it.
                val usage = when (val outcome = getTransactionCount(action.id)) {
                    is Outcome.Success -> outcome.value
                    is Outcome.Failure -> 0
                }
                setState { copy(pendingDelete = PendingAccountDelete(card.id, card.name, usage)) }
            }

            AccountsAction.DeleteConfirmed -> {
                val pending = currentState.pendingDelete ?: return
                setState { copy(pendingDelete = null) }
                viewModelScope.launch {
                    when (val result = deleteAccount(pending.id, force = true)) {
                        is Outcome.Success -> sendEvent(
                            AccountsEvent.ShowMessage(
                                UiMessage.Resource(
                                    R.string.account_deleted,
                                    listOf(pending.name),
                                ),
                            ),
                        )

                        is Outcome.Failure -> sendEvent(AccountsEvent.ShowMessage(result.error.toUiMessage()))
                    }
                }
            }

            AccountsAction.DeleteDismissed -> setState { copy(pendingDelete = null) }
        }
    }
}
