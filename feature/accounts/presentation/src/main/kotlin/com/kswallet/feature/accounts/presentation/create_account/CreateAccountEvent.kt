package com.kswallet.feature.accounts.presentation.create_account

import com.kswallet.core.screen.ScreenEvent

sealed interface CreateAccountEvent : ScreenEvent {
    data object AccountCreated : CreateAccountEvent
}