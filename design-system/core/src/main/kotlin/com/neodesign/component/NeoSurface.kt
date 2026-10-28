package com.neodesign.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.neodesign.theme.LocalNeoContentColor
import com.neodesign.theme.NeoTheme

@Composable
fun NeoSurface(
    modifier: Modifier = Modifier,
    color: Color = NeoTheme.colors.surface,
    contentColor: Color = NeoTheme.colors.contentPrimary,
    shape: Shape = NeoTheme.shapes.large,
    border: BorderStroke? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val borderModifier = if (border != null) {
        Modifier.border(border, shape)
    } else {
        Modifier
    }

    CompositionLocalProvider(
        LocalNeoContentColor provides contentColor,
    ) {
        Box(
            modifier = modifier
                .clip(shape)
                .background(color)
                .then(borderModifier),
            content = content,
        )
    }
}