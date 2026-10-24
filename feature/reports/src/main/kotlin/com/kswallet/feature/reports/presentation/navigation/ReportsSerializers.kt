package com.kswallet.feature.reports.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val reportsNavigationSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(ReportsRoute::class)
    }
}
