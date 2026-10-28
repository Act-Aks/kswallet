package com.kswallet.app.ui

import androidx.compose.runtime.Composable
import com.kswallet.app.foundation.FoundationRoute
import com.neodesign.theme.NeoTheme

@Composable
fun KsWalletApp() {
    NeoTheme {
        FoundationRoute()
    }
}