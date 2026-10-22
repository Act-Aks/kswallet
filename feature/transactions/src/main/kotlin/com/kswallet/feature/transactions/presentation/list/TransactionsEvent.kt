package com.kswallet.feature.transactions.presentation.list

import com.kswallet.core.model.TransactionId
import com.kswallet.core.ui.screen.ScreenEvent
import com.kswallet.core.ui.util.UiMessage

sealed interface TransactionsEvent : ScreenEvent {
    data class OpenEditor(val transactionId: TransactionId?) : TransactionsEvent
    data class ShowMessage(val message: UiMessage) : TransactionsEvent
}
