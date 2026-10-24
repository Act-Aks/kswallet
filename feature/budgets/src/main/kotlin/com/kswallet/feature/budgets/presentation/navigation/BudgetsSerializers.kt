package com.kswallet.feature.budgets.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val budgetsNavigationSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(BudgetsRoute::class)
        subclass(BudgetEditorRoute::class)
    }
}
