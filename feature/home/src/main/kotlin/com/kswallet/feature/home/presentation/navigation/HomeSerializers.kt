package com.kswallet.feature.home.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val homeNavigationSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(HomeRoute::class)
    }
}
