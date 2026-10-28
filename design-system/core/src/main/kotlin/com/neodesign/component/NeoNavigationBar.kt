package com.neodesign.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.neodesign.theme.NeoTheme

@Composable
fun <K : Any> NeoNavigationBar(
    items: List<NeoNavigationItem<K>>,
    selectedKey: K,
    onSelectionChange: (K) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    require(items.size in 3..5) {
        "NeoNavigationBar supports three to five destinations."
    }

    require(items.map { it.key }.distinct().size == items.size) {
        "NeoNavigationBar item keys must be unique."
    }

    require(items.any { it.key == selectedKey }) {
        "NeoNavigationBar selectedKey must match an item."
    }

    require(items.all { it.label.isNotBlank() }) {
        "NeoNavigationBar items require non-blank labels."
    }

    NeoSurface(
        modifier = modifier,
        color = NeoTheme.colors.surfaceRaised,
        shape = NeoTheme.shapes.extraLarge,
        border = BorderStroke(
            width = 1.dp,
            color = NeoTheme.colors.outline,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup()
                .padding(NeoTheme.spacing.extraSmall),
            horizontalArrangement = Arrangement.spacedBy(
                NeoTheme.spacing.extraSmall,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { item ->
                key(item.key) {
                    NavigationItem(
                        item = item,
                        selected = item.key == selectedKey,
                        enabled = enabled && item.enabled,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (item.key != selectedKey) {
                                onSelectionChange(item.key)
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun <K : Any> NavigationItem(
    item: NeoNavigationItem<K>,
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
        colors.background
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

    val containerColor = contentColor
        .copy(alpha = overlayAlpha)
        .compositeOver(baseContainer)

    NeoSurface(
        modifier = modifier
            .selectable(
                selected = selected,
                enabled = enabled,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .defaultMinSize(
                minWidth = 48.dp,
                minHeight = 64.dp,
            ),
        color = containerColor,
        contentColor = contentColor,
        shape = NeoTheme.shapes.pill,
        border = if (enabled && focused) {
            BorderStroke(2.dp, colors.accent)
        } else {
            null
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = NeoTheme.spacing.small,
                    vertical = NeoTheme.spacing.small,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                NeoTheme.spacing.extraSmall,
            ),
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clearAndSetSemantics {},
                contentAlignment = Alignment.Center,
            ) {
                item.icon(selected, contentColor)
            }

            NeoText(
                text = item.label,
                style = NeoTheme.typography.caption.copy(
                    textAlign = TextAlign.Center,
                ),
            )
        }
    }
}