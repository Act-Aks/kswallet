package com.kswallet.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Typography: the Material 3 scale with two wallet-specific tweaks - tabular, tighter
 * numerals for money and a heavier `titleLarge` for screen headers.
 */
val KsWalletTypography = Typography().let { base ->
    Typography(
        displayLarge = base.displayLarge.copy(fontWeight = FontWeight.Bold),
        headlineMedium = base.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
        titleLarge = base.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        labelSmall = base.labelSmall.copy(fontWeight = FontWeight.Medium),
    )
}

/** Amounts are always rendered with a monospaced digit set so columns line up. */
val MoneyTextStyle: TextStyle = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
)

val MoneyHeaderTextStyle: TextStyle = MoneyTextStyle.copy(fontSize = 34.sp)
