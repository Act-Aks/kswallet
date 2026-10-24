package com.kswallet.feature.budgets.presentation.navigation

import com.kswallet.core.navigation.navigationSerializersModule

val budgetsNavigationSerializersModule = navigationSerializersModule(
    BudgetsRoute::class,
    BudgetEditorRoute::class,
)
