package com.neodesign.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.neodesign.theme.NeoTheme

@Composable
fun NeoTextField(
    state: TextFieldState,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    supportingText: String? = null,
    errorMessage: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    inputTransformation: InputTransformation? = null,
    outputTransformation: OutputTransformation? = null,
    onImeAction: (() -> Unit)? = null,
) {
    require(label.isNotBlank()) {
        "NeoTextField requires a non-blank label."
    }

    require(errorMessage == null || errorMessage.isNotBlank()) {
        "NeoTextField errorMessage must be null or non-blank."
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

    val borderWidth = if (enabled && focused) {
        2.dp
    } else {
        1.dp
    }

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

        BasicTextField(
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
            readOnly = readOnly,
            textStyle = NeoTheme.typography.body.copy(
                color = contentColor,
            ),
            keyboardOptions = keyboardOptions,
            onKeyboardAction = keyboardActionHandler,
            lineLimits = lineLimits,
            inputTransformation = inputTransformation,
            outputTransformation = outputTransformation,
            interactionSource = interactionSource,
            cursorBrush = SolidColor(
                if (hasError) colors.negative else colors.accent,
            ),
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
                        width = borderWidth,
                        color = borderColor,
                    ),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(NeoTheme.spacing.large),
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