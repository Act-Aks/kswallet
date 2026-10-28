package com.neodesign.component

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.neodesign.theme.LocalNeoContentColor
import com.neodesign.theme.LocalNeoTextStyle
import com.neodesign.theme.NeoTheme

@Composable
fun NeoText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalNeoTextStyle.current,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    val resolvedColor = when {
        color != Color.Unspecified -> color
        style.color != Color.Unspecified -> style.color
        LocalNeoContentColor.current != Color.Unspecified ->
            LocalNeoContentColor.current

        else -> NeoTheme.colors.contentPrimary
    }

    BasicText(
        text = text,
        modifier = modifier,
        style = style.merge(
            TextStyle(color = resolvedColor),
        ),
        maxLines = maxLines,
        overflow = overflow,
    )
}