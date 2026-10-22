package com.kswallet.feature.reports.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kswallet.core.navigation.KsWalletDestination
import com.kswallet.feature.reports.presentation.reports.ReportsRoute
import kotlinx.serialization.Serializable

/** Destinations owned by the reports feature. */
@Serializable
data object ReportsDestination : KsWalletDestination

fun NavGraphBuilder.reportsScreen(onNavigateBack: () -> Unit) {
    composable<ReportsDestination> {
        ReportsRoute(onNavigateBack = onNavigateBack)
    }
}
