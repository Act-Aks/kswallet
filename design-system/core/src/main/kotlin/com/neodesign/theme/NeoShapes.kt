package com.neodesign.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Immutable
data class NeoShapes(
    val small: Shape = RoundedCornerShape(8.dp),
    val medium: Shape = RoundedCornerShape(12.dp),
    val large: Shape = RoundedCornerShape(20.dp),
    val extraLarge: Shape = RoundedCornerShape(28.dp),
    val pill: Shape = RoundedCornerShape(percent = 50),
)