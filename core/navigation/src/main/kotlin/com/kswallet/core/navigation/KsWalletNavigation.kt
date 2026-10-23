package com.kswallet.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * Every destination in KsWallet is a serializable Navigation 3 key - no string routes.
 *
 * Features own their keys and contribute `EntryProviderScope<NavKey>` entry builders. The app
 * composes those builders and owns the back stack; feature modules only depend on contracts
 * they need to reach.
 */
interface KsWalletDestination : NavKey

/** Marks a destination reachable from the bottom navigation bar. */
interface TopLevelDestination : KsWalletDestination {
    val route: KsWalletDestination
    val labelRes: Int
}

/** Pushes a typed key onto the back stack, avoiding a duplicate rapid tap on the current key. */
fun NavBackStack<NavKey>.navigateTo(destination: KsWalletDestination) {
    if (lastOrNull() != destination) add(destination)
}

/** Pops one entry without ever removing the root of a tab's stack. */
fun NavBackStack<NavKey>.popBackStack(): Boolean {
    if (size <= 1) return false
    removeLastOrNull()
    return true
}

/** True when [destination] is the currently visible entry. */
fun NavBackStack<NavKey>.isOnDestination(destination: KsWalletDestination): Boolean =
    lastOrNull()?.let { it::class == destination::class } == true
