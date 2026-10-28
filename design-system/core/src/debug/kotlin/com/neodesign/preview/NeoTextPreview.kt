package com.neodesign.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neodesign.component.NeoText

@Preview(
    name = "NeoText — light",
    showBackground = true,
)
@Composable
private fun NeoTextLightPreview() {
    Box(
        modifier = Modifier
            .background(Color.White)
            .padding(24.dp),
    ) {
        NeoText(
            text = "NeoDesign",
            style = TextStyle(
                color = Color.Black,
                fontSize = 24.sp,
            ),
        )
    }
}

@Preview(
    name = "NeoText — dark",
    showBackground = true,
)
@Composable
private fun NeoTextDarkPreview() {
    Box(
        modifier = Modifier
            .background(Color.Black)
            .padding(24.dp),
    ) {
        NeoText(
            text = "NeoDesign",
            style = TextStyle(
                color = Color.White,
                fontSize = 24.sp,
            ),
        )
    }
}