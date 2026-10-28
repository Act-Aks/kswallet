package com.kswallet.app.foundation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.kswallet.app.R
import com.neodesign.component.NeoButton
import com.neodesign.component.NeoButtonVariant
import com.neodesign.component.NeoCard
import com.neodesign.component.NeoText
import com.neodesign.theme.NeoTheme

@Composable
fun FoundationScreen(
    state: FoundationState,
    resetNoticeVisible: Boolean,
    onAction: (FoundationAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NeoTheme.colors.background)
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(NeoTheme.spacing.extraLarge),
        verticalArrangement = Arrangement.spacedBy(
            NeoTheme.spacing.extraLarge,
        ),
    ) {
        NeoText(
            text = stringResource(R.string.app_name),
            style = NeoTheme.typography.display,
        )

        NeoText(
            text = stringResource(R.string.foundation_title),
            style = NeoTheme.typography.title,
        )

        NeoText(
            text = stringResource(R.string.foundation_description),
            color = NeoTheme.colors.contentSecondary,
        )

        NeoCard(
            modifier = Modifier.fillMaxWidth(),
        ) {
            NeoText(
                text = stringResource(
                    R.string.foundation_count,
                    state.count,
                    state.maximum,
                ),
                style = NeoTheme.typography.headline,
            )
        }

        NeoButton(
            text = stringResource(R.string.foundation_increment),
            onClick = {
                onAction(FoundationAction.IncrementClicked)
            },
            enabled = state.canIncrement,
            modifier = Modifier.fillMaxWidth(),
        )

        NeoButton(
            text = stringResource(R.string.foundation_reset),
            onClick = {
                onAction(FoundationAction.ResetClicked)
            },
            variant = NeoButtonVariant.Outlined,
            modifier = Modifier.fillMaxWidth(),
        )

        if (resetNoticeVisible) {
            NeoText(
                text = stringResource(
                    R.string.foundation_reset_completed,
                ),
                modifier = Modifier.semantics {
                    liveRegion = LiveRegionMode.Polite
                },
                color = NeoTheme.colors.positive,
            )
        }
    }
}