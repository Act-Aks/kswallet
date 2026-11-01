package com.kswallet.feature.accounts.presentation.edit_account

import com.kswallet.core.screen.ScreenEvent

sealed interface EditAccountEvent : ScreenEvent {
    data object AccountUpdated : EditAccountEvent
}