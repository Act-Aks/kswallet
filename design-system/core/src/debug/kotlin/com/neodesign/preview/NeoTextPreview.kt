package com.neodesign.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.neodesign.component.NeoText
import com.neodesign.theme.NeoTheme

@Preview(name = "NeoText — light")
@Composable
private fun NeoTextLightPreview() {
    TextSample(darkTheme = false)
}

@Preview(name = "NeoText — dark")
@Composable
private fun NeoTextDarkPreview() {
    TextSample(darkTheme = true)
}

@Composable
private fun TextSample(darkTheme: Boolean) {
    NeoTheme(darkTheme = darkTheme) {
        Box(
            modifier = Modifier
                .background(NeoTheme.colors.background)
                .padding(NeoTheme.spacing.extraLarge),
        ) {
            NeoText(
                text = "NeoDesign",
                style = NeoTheme.typography.headline,
            )
        }
    }
}