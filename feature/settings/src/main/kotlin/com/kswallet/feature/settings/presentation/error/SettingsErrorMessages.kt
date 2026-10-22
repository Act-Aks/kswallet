package com.kswallet.feature.settings.presentation.error

import com.kswallet.core.common.result.DomainError
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.feature.settings.R

/** One place, per feature, that turns a failure into words. */
fun DomainError.toUiMessage(): UiMessage = when (this) {
    DomainError.NotFound -> UiMessage.Resource(R.string.error_not_found)
    is DomainError.Conflict -> UiMessage.Resource(R.string.error_storage)
    is DomainError.Data -> UiMessage.Resource(R.string.error_storage)
    is DomainError.Unknown -> UiMessage.Resource(R.string.error_unknown)
}
