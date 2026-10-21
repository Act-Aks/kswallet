package com.kswallet.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * KsWallet brand palette.
 *
 * Money apps live or die by one rule: the two directions of cash flow must be readable
 * at a glance and must not rely on color alone (see `KsAmountText`, which adds a sign).
 */
object KsWalletColors {
    val Mint = Color(0xFF0E7C66)
    val MintLight = Color(0xFF6FD8BE)
    val MintContainer = Color(0xFFB9F0E1)
    val Ink = Color(0xFF10241F)
    val Sand = Color(0xFFF7F5F0)

    val Income = Color(0xFF1B873F)
    val IncomeDark = Color(0xFF6FE39A)
    val Expense = Color(0xFFC0392B)
    val ExpenseDark = Color(0xFFFF9B8F)
    val Transfer = Color(0xFF2E6BE6)
    val TransferDark = Color(0xFF9CBBFF)
}

/** Colors offered to accounts and categories. Index 0 is the default. */
val KsWalletAccentPalette: List<Long> = listOf(
    0xFF0E7C66, // mint
    0xFF2E6BE6, // blue
    0xFF6A4CE0, // violet
    0xFFC0392B, // red
    0xFFE07A1F, // orange
    0xFFE0B01F, // amber
    0xFF1B873F, // green
    0xFF0F8B8D, // teal
    0xFFB03A8B, // magenta
    0xFF5C6B73, // slate
)

val KsWalletLightColorScheme = lightColorScheme(
    primary = KsWalletColors.Mint,
    onPrimary = Color.White,
    primaryContainer = KsWalletColors.MintContainer,
    onPrimaryContainer = KsWalletColors.Ink,
    secondary = KsWalletColors.Transfer,
    onSecondary = Color.White,
    background = KsWalletColors.Sand,
    onBackground = KsWalletColors.Ink,
    surface = Color.White,
    onSurface = KsWalletColors.Ink,
    surfaceVariant = Color(0xFFE9E5DC),
    onSurfaceVariant = Color(0xFF4A5652),
    error = KsWalletColors.Expense,
    onError = Color.White,
)

val KsWalletDarkColorScheme = darkColorScheme(
    primary = KsWalletColors.MintLight,
    onPrimary = Color(0xFF00201A),
    primaryContainer = Color(0xFF115E4D),
    onPrimaryContainer = KsWalletColors.MintContainer,
    secondary = KsWalletColors.TransferDark,
    onSecondary = Color(0xFF00204A),
    background = Color(0xFF0B1512),
    onBackground = Color(0xFFE2E4E0),
    surface = Color(0xFF111C19),
    onSurface = Color(0xFFE2E4E0),
    surfaceVariant = Color(0xFF2A3833),
    onSurfaceVariant = Color(0xFFBFC9C4),
    error = KsWalletColors.ExpenseDark,
    onError = Color(0xFF3B0A05),
)
