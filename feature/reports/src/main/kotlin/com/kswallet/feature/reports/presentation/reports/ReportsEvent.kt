package com.kswallet.feature.reports.presentation.reports

import com.kswallet.core.ui.screen.ScreenEvent
import com.kswallet.core.ui.util.UiMessage

sealed interface ReportsEvent : ScreenEvent {
    data class ShowMessage(val message: UiMessage) : ReportsEvent
}
