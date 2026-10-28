package com.neodesign.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.neodesign.R
import com.neodesign.theme.LocalNeoContentColor
import com.neodesign.theme.NeoTheme

@Composable
fun NeoSecureTextField(
    state: TextFieldState,
    label: String,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    supportingText: String? = null,
    errorMessage: String? = null,
    enabled: Boolean = true,
    inputTransformation: InputTransformation? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.None,
        autoCorrectEnabled = false,
        keyboardType = KeyboardType.Password,
    ),
    onImeAction: (() -> Unit)? = null,
) {
    require(label.isNotBlank()) {
        "NeoSecureTextField requires a non-blank label."
    }

    require(errorMessage == null || errorMessage.isNotBlank()) {
        "NeoSecureTextField errorMessage must be null or non-blank."
    }

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val focused by interactionSource.collectIsFocusedAsState()

    val colors = NeoTheme.colors
    val hasError = errorMessage != null

    val contentColor = if (enabled) {
        colors.contentPrimary
    } else {
        colors.contentSecondary.copy(alpha = 0.55f)
    }

    val secondaryColor = if (enabled) {
        colors.contentSecondary
    } else {
        colors.contentSecondary.copy(alpha = 0.55f)
    }

    val borderColor = when {
        !enabled -> colors.outline
        hasError -> colors.negative
        focused -> colors.accent
        else -> colors.outline
    }

    val visibilityLabel = stringResource(
        if (passwordVisible) {
            R.string.neo_hide_password
        } else {
            R.string.neo_show_password
        },
    )

    val keyboardActionHandler = onImeAction?.let { callback ->
        KeyboardActionHandler { performDefaultAction ->
            callback()
            performDefaultAction()
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(
            NeoTheme.spacing.small,
        ),
    ) {
        NeoText(
            text = label,
            modifier = Modifier.clearAndSetSemantics {},
            color = secondaryColor,
            style = NeoTheme.typography.label,
        )

        BasicSecureTextField(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = label

                    if (errorMessage != null) {
                        error(errorMessage)
                    }
                },
            enabled = enabled,
            textStyle = NeoTheme.typography.body.copy(
                color = contentColor,
            ),
            inputTransformation = inputTransformation,
            keyboardOptions = keyboardOptions,
            onKeyboardAction = keyboardActionHandler,
            interactionSource = interactionSource,
            cursorBrush = SolidColor(
                if (hasError) colors.negative else colors.accent,
            ),
            textObfuscationMode = if (passwordVisible) {
                TextObfuscationMode.Visible
            } else {
                TextObfuscationMode.Hidden
            },
            decorator = { innerTextField ->
                NeoSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 56.dp),
                    color = if (enabled) {
                        colors.surface
                    } else {
                        colors.surfaceRaised
                    },
                    contentColor = contentColor,
                    shape = NeoTheme.shapes.medium,
                    border = BorderStroke(
                        width = if (enabled && focused) {
                            2.dp
                        } else {
                            1.dp
                        },
                        color = borderColor,
                    ),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(NeoTheme.spacing.extraSmall),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(
                                    horizontal = NeoTheme.spacing.medium,
                                    vertical = NeoTheme.spacing.small,
                                ),
                        ) {
                            if (
                                state.text.isEmpty() &&
                                placeholder != null
                            ) {
                                NeoText(
                                    text = placeholder,
                                    modifier = Modifier
                                        .clearAndSetSemantics {},
                                    color = secondaryColor,
                                )
                            }

                            innerTextField()
                        }

                        NeoIconButton(
                            contentDescription = visibilityLabel,
                            onClick = {
                                onPasswordVisibilityChange(
                                    !passwordVisible,
                                )
                            },
                            enabled = enabled,
                            variant = NeoButtonVariant.Outlined,
                        ) {
                            PasswordVisibilityIcon(
                                passwordVisible = passwordVisible,
                            )
                        }
                    }
                }
            },
        )

        val message = errorMessage ?: supportingText

        if (message != null) {
            NeoText(
                text = message,
                color = if (hasError && enabled) {
                    colors.negative
                } else {
                    secondaryColor
                },
                style = NeoTheme.typography.caption,
            )
        }
    }
}

@Composable
private fun PasswordVisibilityIcon(
    passwordVisible: Boolean,
) {
    val inheritedColor = LocalNeoContentColor.current

    val color = if (inheritedColor != Color.Unspecified) {
        inheritedColor
    } else {
        NeoTheme.colors.contentPrimary
    }

    Canvas(
        modifier = Modifier.size(24.dp),
    ) {
        val strokeWidth = 1.8.dp.toPx()

        drawOval(
            color = color,
            topLeft = Offset(
                x = size.width * 0.08f,
                y = size.height * 0.25f,
            ),
            size = Size(
                width = size.width * 0.84f,
                height = size.height * 0.5f,
            ),
            style = Stroke(width = strokeWidth),
        )

        drawCircle(
            color = color,
            radius = size.minDimension * 0.12f,
            center = center,
            style = Stroke(width = strokeWidth),
        )

        if (passwordVisible) {
            drawLine(
                color = color,
                start = Offset(
                    size.width * 0.16f,
                    size.height * 0.16f,
                ),
                end = Offset(
                    size.width * 0.84f,
                    size.height * 0.84f,
                ),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round,
            )
        }
    }
}