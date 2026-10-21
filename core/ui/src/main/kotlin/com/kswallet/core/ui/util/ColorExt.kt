package com.kswallet.core.ui.util

import androidx.compose.ui.graphics.Color

/** Account/category colors are stored as ARGB longs so the database stays UI-agnostic. */
fun Long.toComposeColor(): Color = Color(this)

/** Picks black or white text for a colored container - keeps custom colors readable. */
fun Color.onColorFor(): Color {
    val luminance = (0.299 * red + 0.587 * green + 0.114 * blue)
    return if (luminance > 0.6) Color.Black else Color.White
}
