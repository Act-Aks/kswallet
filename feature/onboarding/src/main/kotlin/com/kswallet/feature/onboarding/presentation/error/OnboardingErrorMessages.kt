package com.kswallet.feature.onboarding.presentation.error

import com.kswallet.core.common.result.DomainError
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.feature.onboarding.R

internal fun DomainError.toUiMessage(): UiMessage = when (this) {
    is DomainError.Data -> UiMessage.Resource(R.string.onboarding_error_storage)
    else -> UiMessage.Resource(R.string.onboarding_error_generic)
}
