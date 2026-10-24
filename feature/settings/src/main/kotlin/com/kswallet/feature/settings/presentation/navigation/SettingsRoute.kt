package com.kswallet.feature.settings.presentation.navigation

import com.kswallet.core.navigation.KsWalletRoute
import kotlinx.serialization.Serializable

/** Settings is not a tab: it is pushed from the dashboard's top bar. */
@Serializable
data object SettingsRoute : KsWalletRoute
