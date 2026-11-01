package com.kswallet.app.ui

import androidx.compose.runtime.Composable
import com.kswallet.app.ui.navigation.AccountsNavHost
import com.neodesign.theme.NeoTheme

@Composable
fun KsWalletApp() {
    NeoTheme {
        AccountsNavHost()
    }
}