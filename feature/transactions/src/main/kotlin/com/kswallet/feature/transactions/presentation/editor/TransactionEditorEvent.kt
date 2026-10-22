package com.kswallet.feature.transactions.presentation.editor

import com.kswallet.core.ui.screen.ScreenEvent
import com.kswallet.core.ui.util.UiMessage

sealed interface TransactionEditorEvent : ScreenEvent {
    data class NavigateBack(val saved: Boolean = false) : TransactionEditorEvent
    data class ShowMessage(val message: UiMessage) : TransactionEditorEvent
}
