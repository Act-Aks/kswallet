package com.kswallet.feature.accounts.presentation.editor

import com.kswallet.core.ui.screen.ScreenEvent
import com.kswallet.core.ui.util.UiMessage

sealed interface AccountEditorEvent : ScreenEvent {
    data class NavigateBack(val saved: Boolean = false) : AccountEditorEvent
    data class ShowMessage(val message: UiMessage) : AccountEditorEvent
}
