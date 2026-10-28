package com.neodesign.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.neodesign.theme.NeoTheme

@Composable
fun NeoLoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color = NeoTheme.colors.contentPrimary,
) {
    val transition = rememberInfiniteTransition(
        label = "NeoLoadingIndicator",
    )

    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 900,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "Rotation",
    )

    Canvas(
        modifier = modifier
            .size(20.dp)
            .semantics {
                progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate
            },
    ) {
        val strokeWidth = 2.dp.toPx()
        val inset = strokeWidth / 2f

        val arcSize = Size(
            width = (size.width - strokeWidth).coerceAtLeast(0f),
            height = (size.height - strokeWidth).coerceAtLeast(0f),
        )

        drawArc(
            color = color.copy(alpha = 0.18f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = arcSize,
            style = Stroke(width = strokeWidth),
        )

        drawArc(
            color = color,
            startAngle = rotation,
            sweepAngle = 100f,
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = arcSize,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
            ),
        )
    }
}