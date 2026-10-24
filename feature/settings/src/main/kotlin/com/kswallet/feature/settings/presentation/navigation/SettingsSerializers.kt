package com.kswallet.feature.settings.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val settingsNavigationSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(SettingsRoute::class)
    }
}
