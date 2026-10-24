package com.kswallet.app.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import com.kswallet.feature.accounts.presentation.navigation.accountsNavigationSerializersModule
import com.kswallet.feature.budgets.presentation.navigation.budgetsNavigationSerializersModule
import com.kswallet.feature.categories.presentation.navigation.categoriesNavigationSerializersModule
import com.kswallet.feature.home.presentation.navigation.homeNavigationSerializersModule
import com.kswallet.feature.reports.presentation.navigation.reportsNavigationSerializersModule
import com.kswallet.feature.settings.presentation.navigation.settingsNavigationSerializersModule
import com.kswallet.feature.transactions.presentation.navigation.transactionsNavigationSerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val ksWalletNavigationSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        include(accountsNavigationSerializersModule)
        include(budgetsNavigationSerializersModule)
        include(categoriesNavigationSerializersModule)
        include(homeNavigationSerializersModule)
        include(reportsNavigationSerializersModule)
        include(settingsNavigationSerializersModule)
        include(transactionsNavigationSerializersModule)
    }
}

val ksWalletSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = ksWalletNavigationSerializersModule
}
