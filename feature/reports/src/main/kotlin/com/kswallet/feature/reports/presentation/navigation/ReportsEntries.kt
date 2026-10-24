package com.kswallet.feature.reports.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kswallet.feature.reports.presentation.reports.ReportsRoot

fun EntryProviderScope<NavKey>.reportsEntries(
    onNavigateBack: () -> Unit,
) {
    entry<ReportsRoute> {
        ReportsRoot(onNavigateBack = onNavigateBack)
    }
}
