package com.kswallet.feature.reports.presentation.reports

import com.kswallet.core.ui.screen.ScreenAction

sealed interface ReportsAction : ScreenAction {
    data object PreviousMonthClicked : ReportsAction
    data object NextMonthClicked : ReportsAction
    data object RetryClicked : ReportsAction
}
