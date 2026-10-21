package com.kswallet.core.ui.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

/**
 * User-facing text that survives the trip from a ViewModel to the UI.
 *
 * ViewModels never touch `Context` or `Resources` (they stay unit-testable on the JVM);
 * they hand over a resource id, and the composable resolves it with the real locale.
 */
@Immutable
sealed interface UiMessage {

    data class Plain(val text: String) : UiMessage

    @Immutable
    data class Resource(@StringRes val id: Int, val args: List<Any> = emptyList()) : UiMessage

    companion object {
        fun plain(text: String): UiMessage = Plain(text)
    }
}
