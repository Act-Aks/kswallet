package com.kswallet.feature.accounts.presentation.navigation

import com.kswallet.core.navigation.KsWalletRoute
import kotlinx.serialization.Serializable

@Serializable
data object AccountsRoute : KsWalletRoute

@Serializable
data class AccountEditorRoute(val accountId: Long? = null) : KsWalletRoute

@Serializable
data class AccountDetailRoute(val accountId: Long) : KsWalletRoute
