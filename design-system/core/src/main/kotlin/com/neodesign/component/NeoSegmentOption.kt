package com.neodesign.component

data class NeoSegmentOption<K : Any>(
    val key: K,
    val label: String,
    val enabled: Boolean = true,
)

enum class NeoSegmentedControlLayout {
    Adaptive,
    Horizontal,
    Vertical,
}