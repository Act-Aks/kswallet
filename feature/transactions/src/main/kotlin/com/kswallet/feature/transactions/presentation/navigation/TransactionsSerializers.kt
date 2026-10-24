package com.kswallet.feature.transactions.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val transactionsNavigationSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(TransactionsRoute::class)
        subclass(TransactionEditorRoute::class)
    }
}
