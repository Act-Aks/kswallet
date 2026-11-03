package com.kswallet.app.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import com.kswallet.feature.accounts.presentation.navigation.accountsNavigationSerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

/**
 * Combines route serializers from all app features.
 *
 * Shared by onboarding and wallet navigation state. Each feature module
 * registers its own routes as polymorphic subclasses of NavKey.
 */
val ksWalletNavigationSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        include(accountsNavigationSerializersModule)
    }
}

/**
 * Configures serialization and restoration of app navigation keys.
 */
val ksWalletSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = ksWalletNavigationSerializersModule
}