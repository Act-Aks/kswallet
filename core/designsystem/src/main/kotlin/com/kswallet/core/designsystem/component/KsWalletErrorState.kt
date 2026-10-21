package com.kswallet.core.designsystem.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** Inline error with a retry affordance. */
@Composable
fun KsWalletErrorState(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
) {
    KsWalletEmptyState(
        title = "Something went wrong",
        message = message,
        icon = Icons.Filled.Warning,
        modifier = modifier,
        actionLabel = if (onRetry != null) "Retry" else null,
        onAction = onRetry,
    )
}
