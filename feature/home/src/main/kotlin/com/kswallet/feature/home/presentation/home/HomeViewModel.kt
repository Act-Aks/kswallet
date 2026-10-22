package com.kswallet.feature.home.presentation.home

import androidx.lifecycle.viewModelScope
import com.kswallet.core.datastore.domain.repository.SettingsRepository
import com.kswallet.core.ui.screen.ScreenViewModel
import com.kswallet.feature.home.domain.usecase.ObserveHomeSummaryUseCase
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class HomeViewModel(
    observeHomeSummary: ObserveHomeSummaryUseCase,
    settingsRepository: SettingsRepository,
) : ScreenViewModel<HomeState, HomeAction, HomeEvent>(HomeState()) {

    init {
        settingsRepository.observeSettings()
            .map { it.defaultCurrency }
            .distinctUntilChanged()
            .onEach { currency ->
                observeHomeSummary(currency)
                    .onEach { summary ->
                        setState {
                            copy(
                                isLoading = false,
                                currency = currency,
                                totalBalance = summary.totalBalance,
                                monthIncome = summary.monthIncome,
                                monthExpense = summary.monthExpense,
                                netThisMonth = summary.monthIncome - summary.monthExpense,
                                accountCount = summary.accountCount,
                                recentTransactions = summary.recentTransactions.map { details ->
                                    RecentTransactionUi(
                                        id = details.id.value,
                                        title = details.categoryName
                                            ?: details.transferAccountName
                                            ?: details.accountName,
                                        subtitle = details.note
                                            ?: details.transferAccountName?.let { "${details.accountName} \u2192 $it" }
                                            ?: details.accountName,
                                        amount = details.amount,
                                        type = details.type,
                                        iconKey = details.categoryIconKey,
                                        colorArgb = details.categoryColorArgb,
                                    )
                                },
                            )
                        }
                    }
                    .launchIn(viewModelScope)
            }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.AddTransactionClicked -> sendEvent(HomeEvent.OpenTransactionEditor(null))

            HomeAction.AddAccountClicked -> sendEvent(HomeEvent.OpenAccountEditor)

            HomeAction.SeeAllTransactionsClicked -> sendEvent(HomeEvent.NavigateToTransactions)

            HomeAction.SeeAccountsClicked -> sendEvent(HomeEvent.NavigateToAccounts)

            is HomeAction.TransactionClicked -> sendEvent(HomeEvent.OpenTransactionEditor(action.id))
        }
    }
}
