package com.kswallet.app.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kswallet.core.navigation.Navigator
import com.kswallet.feature.accounts.presentation.navigation.accountsEntries

/**
 * Registers wallet destinations and connects feature navigation callbacks
 * to the app's [Navigator].
 *
 * Cross-feature route knowledge remains in the app module rather than
 * leaking into individual feature implementations.
 */
internal fun EntryProviderScope<NavKey>.ksWalletEntries(navigator: Navigator) {
    accountsEntries(
        onNavigateBack = navigator::goBack,
        onAccountUpdate = navigator::goBack,
        onAccountCreate = navigator::goBack,
        onOpenAccount = { },
    )
}