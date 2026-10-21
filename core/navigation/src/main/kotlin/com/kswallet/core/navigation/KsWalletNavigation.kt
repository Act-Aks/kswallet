package com.kswallet.core.navigation

import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavOptionsBuilder
import kotlinx.serialization.Serializable

/**
 * Every destination in KsWallet is a serializable type - no string routes anywhere.
 *
 * A feature declares its destinations in `presentation/navigation/<Feature>Navigation.kt`
 * and exposes one `NavGraphBuilder.<feature>Screen(...)` extension plus a `navigate`
 * helper. The app module assembles those graphs; features never know about each other's
 * screens, only about [KsWalletDestination] contracts they need to reach.
 */
interface KsWalletDestination

/** Marks a destination reachable from the bottom navigation bar. */
interface TopLevelDestination : KsWalletDestination {
    val route: KsWalletDestination
    val labelRes: Int
}

/** Navigates to a typed destination, ignoring rapid double taps. */
fun NavController.navigateTo(destination: KsWalletDestination, builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(destination, builder)
}

/**
 * True when [destination] (or one of its parents) is the current destination.
 *
 * The comparison is made on the serialized route name rather than on
 * `NavDestination.hasRoute(KClass)`: navigation 2.9 declares a *member*
 * `hasRoute(String, Bundle)` which shadows that extension, so the route string is the
 * reliable way to ask the question (and it is what the destination is matched on anyway).
 * A route with arguments looks like `com.x.MyDestination/{id}`, hence the prefix comparison.
 */
fun NavController.isOnDestination(destination: KsWalletDestination): Boolean {
    val routeName = destination::class.qualifiedName ?: return false
    return currentDestination?.hierarchy?.any { it.route?.substringBefore('/') == routeName } == true
}

/** Serializable contract for the "open the transaction editor" hand-off between features. */
@Serializable
data class TransactionEditorDestination(val transactionId: Long? = null) : KsWalletDestination

/** Serializable contract for the "open the account detail" hand-off between features. */
@Serializable
data class AccountDetailDestination(val accountId: Long) : KsWalletDestination
