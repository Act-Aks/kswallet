package com.kswallet.feature.categories.presentation.editor

import com.kswallet.core.ui.screen.ScreenEvent
import com.kswallet.core.ui.util.UiMessage

sealed interface CategoryEditorEvent : ScreenEvent {
    data class NavigateBack(val savedOrDeleted: Boolean = false) : CategoryEditorEvent
    data class ShowMessage(val message: UiMessage) : CategoryEditorEvent
}
