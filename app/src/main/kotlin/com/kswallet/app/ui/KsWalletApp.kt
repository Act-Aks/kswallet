package com.kswallet.app.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.kswallet.app.navigation.KsWalletNavigationRoot
import com.neodesign.theme.NeoTheme

@Composable
fun KsWalletApp() {
    NeoTheme(isSystemInDarkTheme()) {
        KsWalletNavigationRoot()
    }
}