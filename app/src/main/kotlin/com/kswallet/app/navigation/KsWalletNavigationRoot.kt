package com.kswallet.app.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.kswallet.core.navigation.Navigator
import com.kswallet.core.navigation.rememberNavigationState
import com.kswallet.core.navigation.toEntries
import com.kswallet.feature.accounts.presentation.navigation.AccountsRoute

/**
 * Hosts onboarding and wallet navigation using a single [NavDisplay].
 *
 * Wallet navigation state remains remembered while onboarding is displayed.
 * Once persisted settings report completion, wallet entries replace onboarding
 * entries without adding onboarding to the wallet's navigation history.
 *
 * This composable must be called only after persisted settings have loaded.
 *
 * @param modifier Modifier applied to the root scaffold.
 */
@Composable
fun KsWalletNavigationRoot(
    modifier: Modifier = Modifier,
) {
    val navigationState = rememberNavigationState(
        startRoute = AccountsRoute,
        topLevelRoutes = emptyList(),
        configuration = ksWalletSavedStateConfiguration,
    )

    val navigator = remember(navigationState) { Navigator(navigationState) }
    val ksWalletEntryProvider = entryProvider { ksWalletEntries(navigator) }
    val activeNavEntries = navigationState.toEntries(ksWalletEntryProvider)

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            onBack = {
                navigator.goBack()
            },
            transitionSpec = {
                slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
            },
            popTransitionSpec = {
                slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
            },
            predictivePopTransitionSpec = {
                slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
            },
            entries = activeNavEntries,
        )
    }
}