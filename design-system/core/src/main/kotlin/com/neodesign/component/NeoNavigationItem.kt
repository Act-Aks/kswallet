package com.neodesign.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class NeoNavigationItem<K : Any>(
    val key: K,
    val label: String,
    val enabled: Boolean = true,
    val icon: @Composable (
        selected: Boolean,
        tint: Color,
    ) -> Unit,
)