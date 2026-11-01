package com.kswallet.app.ui.navigation

import com.kswallet.core.navigation.KsWalletRoute
import kotlinx.serialization.Serializable

@Serializable
data object AccountsRoute : KsWalletRoute

@Serializable
data object CreateAccountRoute : KsWalletRoute

@Serializable
data class EditAccountRoute(val accountId: String) : KsWalletRoute