package com.neodesign.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.neodesign.theme.NeoTheme

enum class NeoCardVariant {
    Standard,
    Raised,
    Outlined,
}

@Composable
fun NeoCard(
    modifier: Modifier = Modifier,
    variant: NeoCardVariant = NeoCardVariant.Standard,
    shape: Shape = NeoTheme.shapes.large,
    contentPadding: PaddingValues = PaddingValues(
        NeoTheme.spacing.extraLarge,
    ),
    content: @Composable ColumnScope.() -> Unit,
) {
    val style = neoCardStyle(
        variant = variant,
        enabled = true,
        pressed = false,
        hovered = false,
        focused = false,
    )

    CardLayout(
        modifier = modifier,
        style = style,
        shape = shape,
        contentPadding = contentPadding,
        content = content,
    )
}

@Composable
fun NeoCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: NeoCardVariant = NeoCardVariant.Standard,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    shape: Shape = NeoTheme.shapes.large,
    contentPadding: PaddingValues = PaddingValues(
        NeoTheme.spacing.extraLarge,
    ),
    content: @Composable ColumnScope.() -> Unit,
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    val pressed by interactionSource.collectIsPressedAsState()
    val hovered by interactionSource.collectIsHoveredAsState()
    val focused by interactionSource.collectIsFocusedAsState()

    val style = neoCardStyle(
        variant = variant,
        enabled = enabled,
        pressed = enabled && pressed,
        hovered = enabled && hovered,
        focused = enabled && focused,
    )

    CardLayout(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClickLabel = onClickLabel,
                role = Role.Button,
                onClick = onClick,
            )
            .defaultMinSize(
                minWidth = 48.dp,
                minHeight = 48.dp,
            ),
        style = style,
        shape = shape,
        contentPadding = contentPadding,
        content = content,
    )
}

@Composable
private fun CardLayout(
    modifier: Modifier,
    style: NeoCardStyle,
    shape: Shape,
    contentPadding: PaddingValues,
    content: @Composable ColumnScope.() -> Unit,
) {
    NeoSurface(
        modifier = modifier,
        color = style.container,
        contentColor = style.content,
        shape = shape,
        border = style.border,
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content,
        )
    }
}

@Immutable
private data class NeoCardStyle(
    val container: Color,
    val content: Color,
    val border: BorderStroke?,
)

@Composable
private fun neoCardStyle(
    variant: NeoCardVariant,
    enabled: Boolean,
    pressed: Boolean,
    hovered: Boolean,
    focused: Boolean,
): NeoCardStyle {
    val colors = NeoTheme.colors

    val baseContainer = when (variant) {
        NeoCardVariant.Standard -> colors.surface
        NeoCardVariant.Raised -> colors.surfaceRaised
        NeoCardVariant.Outlined -> colors.surface
    }

    val contentColor = if (enabled) {
        colors.contentPrimary
    } else {
        colors.contentSecondary.copy(alpha = 0.55f)
    }

    val overlayAlpha = when {
        !enabled -> 0f
        pressed -> 0.08f
        focused -> 0.05f
        hovered -> 0.03f
        else -> 0f
    }

    val containerColor = contentColor
        .copy(alpha = overlayAlpha)
        .compositeOver(baseContainer)

    val border = when {
        enabled && focused ->
            BorderStroke(2.dp, colors.accent)

        variant == NeoCardVariant.Outlined ->
            BorderStroke(1.dp, colors.outline)

        else -> null
    }

    return NeoCardStyle(
        container = containerColor,
        content = contentColor,
        border = border,
    )
}