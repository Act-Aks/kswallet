package com.neodesign.component.internal

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import com.neodesign.component.NeoButtonVariant
import com.neodesign.theme.NeoTheme

@Immutable
internal data class NeoActionStyle(
    val container: Color,
    val content: Color,
    val border: BorderStroke?,
)

@Composable
internal fun neoActionStyle(
    variant: NeoButtonVariant,
    enabled: Boolean,
    pressed: Boolean,
    hovered: Boolean,
    focused: Boolean,
): NeoActionStyle {
    val colors = NeoTheme.colors

    if (!enabled) {
        return NeoActionStyle(
            container = colors.surfaceRaised,
            content = colors.contentSecondary.copy(alpha = 0.55f),
            border = if (
                variant == NeoButtonVariant.Outlined ||
                variant == NeoButtonVariant.Destructive
            ) {
                BorderStroke(1.dp, colors.outline)
            } else {
                null
            },
        )
    }

    val base = when (variant) {
        NeoButtonVariant.Primary -> NeoActionStyle(
            container = colors.action,
            content = colors.onAction,
            border = null,
        )

        NeoButtonVariant.Secondary -> NeoActionStyle(
            container = colors.surfaceRaised,
            content = colors.contentPrimary,
            border = null,
        )

        NeoButtonVariant.Outlined -> NeoActionStyle(
            container = colors.surface,
            content = colors.contentPrimary,
            border = BorderStroke(1.dp, colors.outline),
        )

        NeoButtonVariant.Destructive -> NeoActionStyle(
            container = colors.surface,
            content = colors.negative,
            border = BorderStroke(1.dp, colors.negative),
        )
    }

    val overlayAlpha = when {
        pressed -> 0.12f
        focused -> 0.08f
        hovered -> 0.05f
        else -> 0f
    }

    val container = base.content
        .copy(alpha = overlayAlpha)
        .compositeOver(base.container)

    return base.copy(
        container = container,
        border = if (focused) {
            BorderStroke(2.dp, colors.accent)
        } else {
            base.border
        },
    )
}