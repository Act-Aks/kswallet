package com.neodesign.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.neodesign.component.internal.neoActionStyle
import com.neodesign.theme.NeoTheme

@Composable
fun NeoIconButton(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: NeoButtonVariant = NeoButtonVariant.Secondary,
    enabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    require(contentDescription.isNotBlank()) {
        "NeoIconButton requires a non-blank contentDescription."
    }

    val interactionSource = remember { MutableInteractionSource() }

    val pressed by interactionSource.collectIsPressedAsState()
    val hovered by interactionSource.collectIsHoveredAsState()
    val focused by interactionSource.collectIsFocusedAsState()

    val actionStyle = neoActionStyle(
        variant = variant,
        enabled = enabled,
        pressed = enabled && pressed,
        hovered = enabled && hovered,
        focused = enabled && focused,
    )

    NeoSurface(
        modifier = modifier
            .semantics {
                this.contentDescription = contentDescription
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .defaultMinSize(
                minWidth = 48.dp,
                minHeight = 48.dp,
            ),
        color = actionStyle.container,
        contentColor = actionStyle.content,
        shape = NeoTheme.shapes.pill,
        border = actionStyle.border,
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(NeoTheme.spacing.medium)
                .clearAndSetSemantics {},
            contentAlignment = Alignment.Center,
            content = content,
        )
    }
}