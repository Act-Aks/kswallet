package com.kswallet.feature.transactions.presentation.navigation

import com.kswallet.core.navigation.navigationSerializersModule

val transactionsNavigationSerializersModule = navigationSerializersModule(
    TransactionsRoute::class,
    TransactionEditorRoute::class,
)
