package com.kswallet.feature.transactions.presentation.list

import androidx.lifecycle.viewModelScope
import com.kswallet.core.common.result.Outcome
import com.kswallet.core.common.time.TimeProvider
import com.kswallet.core.datastore.domain.repository.SettingsRepository
import com.kswallet.core.model.Currency
import com.kswallet.core.ui.screen.ScreenViewModel
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.feature.transactions.R
import com.kswallet.feature.transactions.domain.model.TransactionFilter
import com.kswallet.feature.transactions.domain.usecase.DeleteTransactionUseCase
import com.kswallet.feature.transactions.domain.usecase.ObserveCashFlowUseCase
import com.kswallet.feature.transactions.domain.usecase.ObserveTransactionsUseCase
import com.kswallet.feature.transactions.presentation.error.toUiMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * The ledger screen.
 *
 * The filter lives in its own [MutableStateFlow] instead of the UI state: changing it
 * re-subscribes to the database (one query per filter change, indexes intact) and the screen
 * simply follows the resulting stream. `isLoading` is set by the intent so the list does not
 * flicker while the new query runs.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TransactionsViewModel(
    private val observeTransactions: ObserveTransactionsUseCase,
    private val observeCashFlow: ObserveCashFlowUseCase,
    private val deleteTransaction: DeleteTransactionUseCase,
    settingsRepository: SettingsRepository,
    private val timeProvider: TimeProvider,
) : ScreenViewModel<TransactionsState, TransactionsAction, TransactionsEvent>(TransactionsState()) {

    private val filter = MutableStateFlow(TransactionFilter())

    init {
        settingsRepository.observeSettings()
            .map { it.defaultCurrency }
            .distinctUntilChanged()
            .flatMapLatest(::observeScreen)
            .onEach { latest -> setState { latest.copy(pendingDelete = this.pendingDelete) } }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: TransactionsAction) {
        when (action) {
            is TransactionsAction.TypeFilterSelected -> updateFilter { copy(type = action.type) }

            is TransactionsAction.PeriodSelected -> updateFilter { copy(period = action.period) }

            is TransactionsAction.TransactionClicked -> sendEvent(TransactionsEvent.OpenEditor(action.id))

            TransactionsAction.AddTransactionClicked -> sendEvent(TransactionsEvent.OpenEditor(null))

            is TransactionsAction.DeleteClicked -> {
                val item = currentState.dayGroups.flatMap { it.items }.firstOrNull { it.id == action.id }
                    ?: return
                setState { copy(pendingDelete = PendingTransactionDelete(item.id, item.title)) }
            }

            TransactionsAction.DeleteConfirmed -> {
                val pending = currentState.pendingDelete ?: return
                setState { copy(pendingDelete = null) }
                viewModelScope.launch {
                    when (val result = deleteTransaction(pending.id)) {
                        is Outcome.Success -> sendEvent(
                            TransactionsEvent.ShowMessage(UiMessage.Resource(R.string.transaction_deleted)),
                        )

                        is Outcome.Failure -> sendEvent(TransactionsEvent.ShowMessage(result.error.toUiMessage()))
                    }
                }
            }

            TransactionsAction.DeleteDismissed -> setState { copy(pendingDelete = null) }
        }
    }

    /** Re-runs both queries whenever the filter or the base currency changes. */
    private fun observeScreen(currency: Currency): Flow<TransactionsState> =
        filter.flatMapLatest { currentFilter ->
            combine(
                observeTransactions(currentFilter),
                observeCashFlow(currentFilter.period, currency),
            ) { transactions, cashFlow ->
                TransactionsState(
                    isLoading = false,
                    filter = currentFilter,
                    currency = currency,
                    dayGroups = TransactionListMapper.toDayGroups(
                        details = transactions,
                        today = timeProvider.today(),
                        timeZone = timeProvider.timeZone(),
                        currency = currency,
                    ),
                    periodIncome = cashFlow.income,
                    periodExpense = cashFlow.expense,
                )
            }
        }

    private fun updateFilter(reducer: TransactionFilter.() -> TransactionFilter) {
        filter.value = filter.value.reducer()
        setState { copy(isLoading = true) }
    }
}
