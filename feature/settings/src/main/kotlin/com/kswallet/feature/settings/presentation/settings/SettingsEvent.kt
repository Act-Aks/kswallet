package com.kswallet.feature.settings.presentation.settings

import com.kswallet.core.ui.screen.ScreenEvent
import com.kswallet.core.ui.util.UiMessage

sealed interface SettingsEvent : ScreenEvent {
    data class ShowMessage(val message: UiMessage) : SettingsEvent
    data object NavigateBack : SettingsEvent
}
