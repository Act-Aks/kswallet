package com.neodesign.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class NeoColors(
    val background: Color,
    val surface: Color,
    val surfaceRaised: Color,
    val contentPrimary: Color,
    val contentSecondary: Color,
    val outline: Color,
    val action: Color,
    val onAction: Color,
    val accent: Color,
    val onAccent: Color,
    val positive: Color,
    val warning: Color,
    val negative: Color,
)

fun neoLightColors(): NeoColors = NeoColors(
    background = Color(0xFFF6F6F7),
    surface = Color(0xFFFFFFFF),
    surfaceRaised = Color(0xFFECECEF),
    contentPrimary = Color(0xFF151517),
    contentSecondary = Color(0xFF606067),
    outline = Color(0xFFD8D8DE),
    action = Color(0xFF171719),
    onAction = Color(0xFFFFFFFF),
    accent = Color(0xFF3559C7),
    onAccent = Color(0xFFFFFFFF),
    positive = Color(0xFF167345),
    warning = Color(0xFF805400),
    negative = Color(0xFFB32635),
)

fun neoDarkColors(): NeoColors = NeoColors(
    background = Color(0xFF0D0D0F),
    surface = Color(0xFF19191C),
    surfaceRaised = Color(0xFF252529),
    contentPrimary = Color(0xFFF5F5F7),
    contentSecondary = Color(0xFFACACB5),
    outline = Color(0xFF3A3A42),
    action = Color(0xFFF3F3F5),
    onAction = Color(0xFF151517),
    accent = Color(0xFFADC2FF),
    onAccent = Color(0xFF142B68),
    positive = Color(0xFF79DCA0),
    warning = Color(0xFFEAC477),
    negative = Color(0xFFFFA2AC),
)