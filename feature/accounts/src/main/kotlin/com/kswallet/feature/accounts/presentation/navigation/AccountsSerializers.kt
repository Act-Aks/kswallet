package com.kswallet.feature.accounts.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val accountsNavigationSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(AccountsRoute::class)
        subclass(AccountEditorRoute::class)
        subclass(AccountDetailRoute::class)
    }
}
