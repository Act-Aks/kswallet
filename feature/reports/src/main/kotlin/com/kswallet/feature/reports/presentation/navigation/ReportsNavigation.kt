package com.kswallet.feature.reports.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kswallet.core.navigation.KsWalletDestination
import com.kswallet.feature.reports.presentation.reports.ReportsRoute
import kotlinx.serialization.Serializable

/** Destinations owned by the reports feature. */
@Serializable
data object ReportsDestination : KsWalletDestination

fun EntryProviderScope<NavKey>.reportsEntries(onNavigateBack: () -> Unit) {
    entry<ReportsDestination> {
        ReportsRoute(onNavigateBack = onNavigateBack)
    }
}
