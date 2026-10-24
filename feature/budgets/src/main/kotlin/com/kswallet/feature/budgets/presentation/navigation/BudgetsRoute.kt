package com.kswallet.feature.budgets.presentation.navigation

import com.kswallet.core.navigation.KsWalletRoute
import kotlinx.serialization.Serializable

/** Destinations owned by the budgets feature. */
@Serializable
data object BudgetsRoute : KsWalletRoute

@Serializable
data class BudgetEditorRoute(val budgetId: Long? = null) : KsWalletRoute
