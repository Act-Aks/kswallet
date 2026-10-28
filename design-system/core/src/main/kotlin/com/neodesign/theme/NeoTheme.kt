package com.neodesign.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

private val LocalNeoColors = staticCompositionLocalOf<NeoColors> {
    error("NeoTheme is missing. Wrap your content in NeoTheme.")
}

private val LocalNeoTypography = staticCompositionLocalOf<NeoTypography> {
    error("NeoTheme is missing. Wrap your content in NeoTheme.")
}

private val LocalNeoSpacing = staticCompositionLocalOf<NeoSpacing> {
    error("NeoTheme is missing. Wrap your content in NeoTheme.")
}

private val LocalNeoShapes = staticCompositionLocalOf<NeoShapes> {
    error("NeoTheme is missing. Wrap your content in NeoTheme.")
}

internal val LocalNeoContentColor = staticCompositionLocalOf {
    Color.Unspecified
}

internal val LocalNeoTextStyle = staticCompositionLocalOf {
    TextStyle.Default
}

object NeoTheme {

    val colors: NeoColors
        @Composable
        @ReadOnlyComposable
        get() = LocalNeoColors.current

    val typography: NeoTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalNeoTypography.current

    val spacing: NeoSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalNeoSpacing.current

    val shapes: NeoShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalNeoShapes.current
}

@Composable
fun NeoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    colors: NeoColors = if (darkTheme) neoDarkColors() else neoLightColors(),
    typography: NeoTypography = neoTypography(),
    spacing: NeoSpacing = NeoSpacing(),
    shapes: NeoShapes = NeoShapes(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalNeoColors provides colors,
        LocalNeoTypography provides typography,
        LocalNeoSpacing provides spacing,
        LocalNeoShapes provides shapes,
        LocalNeoContentColor provides colors.contentPrimary,
        LocalNeoTextStyle provides typography.body,
        content = content,
    )
}