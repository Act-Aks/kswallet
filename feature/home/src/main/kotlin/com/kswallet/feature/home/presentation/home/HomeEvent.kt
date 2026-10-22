package com.kswallet.feature.home.presentation.home

import com.kswallet.core.ui.screen.ScreenEvent
import com.kswallet.core.ui.util.UiMessage

sealed interface HomeEvent : ScreenEvent {
    data class OpenTransactionEditor(val transactionId: Long?) : HomeEvent
    data object OpenAccountEditor : HomeEvent
    data object NavigateToAccounts : HomeEvent
    data object NavigateToTransactions : HomeEvent
    data class ShowMessage(val message: UiMessage) : HomeEvent
}
