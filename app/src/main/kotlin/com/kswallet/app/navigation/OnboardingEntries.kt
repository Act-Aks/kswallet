package com.kswallet.app.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import com.kswallet.feature.onboarding.presentation.navigation.OnboardingRoute
import com.kswallet.feature.onboarding.presentation.navigation.onboardingEntries

/**
 * Remembers the onboarding stack and prepares its decorated entries.
 *
 * The onboarding feature owns persistence of completion. The app observes
 * that persisted value and switches to wallet entries, so no wallet
 * navigation operation is required in the completion callback.
 *
 * This helper assumes onboarding steps are managed inside the feature's
 * single navigation entry.
 */
@Composable
internal fun rememberOnboardingEntries(): List<NavEntry<NavKey>> {
    val backStack = rememberNavBackStack(
        ksWalletSavedStateConfiguration,
        OnboardingRoute,
    )

    return rememberDecoratedNavEntries(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            onboardingEntries {
                // Intentionally no navigation mutation.
                // Persisted settings drive the transition to the wallet.
            }
        },
    )
}
