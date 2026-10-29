package com.kswallet.app.ui

import androidx.compose.runtime.Composable
import com.kswallet.feature.accounts.presentation.AccountsRoot
import com.neodesign.theme.NeoTheme

@Composable
fun KsWalletApp() {
    NeoTheme {
        AccountsRoot()
    }
}