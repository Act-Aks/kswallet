package com.kswallet.core.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun KsWalletAnimatedEdgeContainer(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = slideInVertically(
            animationSpec = tween(
                durationMillis = 220,
                easing = FastOutSlowInEasing,
            ),
            initialOffsetY = { fullHeight -> fullHeight },
        ) + fadeIn(
            animationSpec = tween(durationMillis = 180),
        ),
        exit = slideOutVertically(
            animationSpec = tween(
                durationMillis = 200,
                easing = FastOutLinearInEasing,
            ),
            targetOffsetY = { fullHeight -> fullHeight },
        ) + fadeOut(
            animationSpec = tween(durationMillis = 160),
        ),
    ) {
        content()
    }
}
