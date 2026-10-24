package com.kswallet.feature.transactions.presentation.navigation

import com.kswallet.core.navigation.KsWalletRoute
import kotlinx.serialization.Serializable

/** Destination owned by the transactions feature. */
@Serializable
data object TransactionsRoute : KsWalletRoute

@Serializable
data class TransactionEditorRoute(val transactionId: Long? = null) : KsWalletRoute
