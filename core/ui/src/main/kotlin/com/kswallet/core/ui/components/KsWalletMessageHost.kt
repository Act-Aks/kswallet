package com.kswallet.core.ui.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import com.kswallet.core.ui.util.UiMessage

/** Resolves a ViewModel-owned [UiMessage] with the real locale, at render time. */
@Composable
fun UiMessage.asText(): String = when (this) {
    is UiMessage.Plain -> text
    is UiMessage.Resource -> stringResource(id = id, *args.toTypedArray())
}

/**
 * Shows [message] once and reports back that it was shown.
 *
 * Screens keep the message in their own state (`var message by remember { mutableStateOf<UiMessage?>(null) }`),
 * which keeps the ViewModel free of Android types and the snackbar free of replay bugs.
 */
@Composable
fun SnackbarHostState.LaunchMessage(
    message: UiMessage?,
    onMessageShown: () -> Unit = {},
) {
    val text = message?.asText()
    LaunchedEffect(text) {
        if (text != null) {
            showSnackbar(text)
            onMessageShown()
        }
    }
}
