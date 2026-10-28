package com.neodesign.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.neodesign.R
import com.neodesign.component.internal.neoActionStyle
import com.neodesign.theme.LocalNeoTextStyle
import com.neodesign.theme.NeoTheme

enum class NeoButtonVariant {
    Primary,
    Secondary,
    Outlined,
    Destructive,
}

@Composable
fun NeoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: NeoButtonVariant = NeoButtonVariant.Primary,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }

    val pressed by interactionSource.collectIsPressedAsState()
    val hovered by interactionSource.collectIsHoveredAsState()
    val focused by interactionSource.collectIsFocusedAsState()

    val interactive = enabled && !loading

    val actionStyle = neoActionStyle(
        variant = variant,
        enabled = enabled,
        pressed = interactive && pressed,
        hovered = interactive && hovered,
        focused = interactive && focused,
    )

    val loadingDescription = stringResource(R.string.neo_loading)

    NeoSurface(
        modifier = modifier
            .semantics {
                if (loading) {
                    stateDescription = loadingDescription
                }
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = interactive,
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
                .padding(
                    horizontal = NeoTheme.spacing.extraLarge,
                    vertical = NeoTheme.spacing.medium,
                ),
            contentAlignment = Alignment.Center,
        ) {
            CompositionLocalProvider(
                LocalNeoTextStyle provides NeoTheme.typography.label,
            ) {
                Row(
                    modifier = Modifier.alpha(
                        if (loading) 0f else 1f,
                    ),
                    horizontalArrangement = Arrangement.spacedBy(
                        NeoTheme.spacing.small,
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (leadingIcon != null) {
                        Box(
                            modifier = Modifier.clearAndSetSemantics {},
                        ) {
                            leadingIcon()
                        }
                    }

                    NeoText(
                        text = text,
                        style = NeoTheme.typography.label.copy(
                            textAlign = TextAlign.Center,
                        ),
                    )
                }
            }

            if (loading) {
                NeoLoadingIndicator(
                    modifier = Modifier.clearAndSetSemantics {},
                    color = actionStyle.content,
                )
            }
        }
    }
}