package com.kswallet.core.navigation

import androidx.navigation3.runtime.NavKey

/**
 * Base contract for every KsWallet Navigation 3 destination.
 *
 * Concrete routes must be `@Serializable` so Navigation 3 can save and
 * restore them as part of the navigation back stack.
 */
interface KsWalletRoute : NavKey