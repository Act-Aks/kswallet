package com.kswallet.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

/**
 * The single theme entry point of the app.
 *
 * `dynamicColor` uses the Android 12+ wallpaper palette when the user asked for it, but
 * the wallet's own income/expense colors always stay the same so that a red amount never
 * turns green because of a wallpaper.
 */
@Composable
fun KsWalletTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> KsWalletDarkColorScheme
        else -> KsWalletLightColorScheme
    }

    CompositionLocalProvider(
        LocalKsWalletSpacing provides KsWalletSpacing,
        LocalKsWalletComponentTokens provides KsWalletComponentTokens(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = KsWalletTypography,
            shapes = KsWalletShapes,
            content = content,
        )
    }
}

val LocalKsWalletSpacing = staticCompositionLocalOf { KsWalletSpacing }
val LocalKsWalletComponentTokens = staticCompositionLocalOf { KsWalletComponentTokens() }

object KsWalletTheme {
    val spacing: KsWalletSpacing get() = KsWalletSpacing
    val components: KsWalletComponentTokens get() = KsWalletComponentTokens()
}
