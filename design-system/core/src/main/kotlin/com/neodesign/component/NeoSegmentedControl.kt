package com.neodesign.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.neodesign.theme.NeoTheme

@Composable
fun <K : Any> NeoSegmentedControl(
    options: List<NeoSegmentOption<K>>,
    selectedKey: K,
    onSelectionChange: (K) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    layout: NeoSegmentedControlLayout = NeoSegmentedControlLayout.Adaptive,
) {
    require(options.isNotEmpty()) {
        "NeoSegmentedControl requires at least one option."
    }

    require(options.map { it.key }.distinct().size == options.size) {
        "NeoSegmentedControl option keys must be unique."
    }

    require(options.any { it.key == selectedKey }) {
        "NeoSegmentedControl selectedKey must match an option."
    }

    require(options.all { it.label.isNotBlank() }) {
        "NeoSegmentedControl options require non-blank labels."
    }

    val spacing = NeoTheme.spacing

    BoxWithConstraints(
        modifier = modifier
            .clip(NeoTheme.shapes.extraLarge)
            .background(NeoTheme.colors.surfaceRaised)
            .padding(spacing.extraSmall),
    ) {
        val gapWidth = spacing.extraSmall * (options.size - 1)
        val availableItemWidth = (maxWidth - gapWidth) / options.size

        val vertical = when (layout) {
            NeoSegmentedControlLayout.Vertical -> true
            NeoSegmentedControlLayout.Horizontal -> false
            NeoSegmentedControlLayout.Adaptive -> constraints.hasBoundedWidth && availableItemWidth < 88.dp
        }

        if (vertical) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(
                    spacing.extraSmall,
                ),
            ) {
                options.forEach { option ->
                    key(option.key) {
                        Segment(
                            option = option,
                            selected = option.key == selectedKey,
                            enabled = enabled && option.enabled,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                if (option.key != selectedKey) {
                                    onSelectionChange(option.key)
                                }
                            },
                        )
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup(),
                horizontalArrangement = Arrangement.spacedBy(
                    spacing.extraSmall,
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                options.forEach { option ->
                    key(option.key) {
                        Segment(
                            option = option,
                            selected = option.key == selectedKey,
                            enabled = enabled && option.enabled,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                if (option.key != selectedKey) {
                                    onSelectionChange(option.key)
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun <K : Any> Segment(
    option: NeoSegmentOption<K>,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    val pressed by interactionSource.collectIsPressedAsState()
    val hovered by interactionSource.collectIsHoveredAsState()
    val focused by interactionSource.collectIsFocusedAsState()

    val colors = NeoTheme.colors

    val contentColor = when {
        !enabled -> colors.contentSecondary.copy(alpha = 0.55f)
        selected -> colors.contentPrimary
        else -> colors.contentSecondary
    }

    val baseContainer = if (selected) {
        colors.surface
    } else {
        colors.surfaceRaised
    }

    val overlayAlpha = when {
        !enabled -> 0f
        pressed -> 0.10f
        focused -> 0.07f
        hovered -> 0.04f
        else -> 0f
    }

    val containerColor = contentColor.copy(alpha = overlayAlpha).compositeOver(baseContainer)

    val border = when {
        enabled && focused -> BorderStroke(2.dp, colors.accent)

        selected -> BorderStroke(1.dp, colors.outline)

        else -> null
    }

    NeoSurface(
        modifier = modifier
            .selectable(
                selected = selected,
                enabled = enabled,
                role = Role.RadioButton,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .defaultMinSize(
                minWidth = 48.dp,
                minHeight = 48.dp,
            ),
        color = containerColor,
        contentColor = contentColor,
        shape = NeoTheme.shapes.large,
        border = border,
    ) {
        NeoText(
            text = option.label,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(
                    horizontal = NeoTheme.spacing.large,
                    vertical = NeoTheme.spacing.medium,
                ),
            style = NeoTheme.typography.label.copy(
                color = Color.Unspecified,
                textAlign = TextAlign.Center,
            ),
        )
    }
}