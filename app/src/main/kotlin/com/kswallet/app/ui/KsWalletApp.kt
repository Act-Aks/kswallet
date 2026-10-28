package com.kswallet.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kswallet.app.R
import com.neodesign.component.NeoText
import com.neodesign.theme.NeoTheme

@Composable
fun KsWalletApp() {
    NeoTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NeoTheme.colors.background)
                .safeDrawingPadding()
                .padding(NeoTheme.spacing.extraLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = NeoTheme.spacing.medium,
                alignment = Alignment.CenterVertically,
            ),
        ) {
            NeoText(
                text = stringResource(R.string.app_name),
                style = NeoTheme.typography.display,
            )

            NeoText(
                text = stringResource(R.string.neodesign_ready),
                color = NeoTheme.colors.contentSecondary,
            )
        }
    }
}