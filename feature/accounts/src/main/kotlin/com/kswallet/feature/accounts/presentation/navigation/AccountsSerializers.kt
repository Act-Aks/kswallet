package com.kswallet.feature.accounts.presentation.navigation

import com.kswallet.core.navigation.navigationSerializersModule

val accountsNavigationSerializersModule = navigationSerializersModule(
    AccountsRoute::class,
    AccountDetailRoute::class,
    AccountEditorRoute::class,
)
