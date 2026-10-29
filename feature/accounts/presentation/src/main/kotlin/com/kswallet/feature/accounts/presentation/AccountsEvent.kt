package com.kswallet.feature.accounts.presentation

import com.kswallet.core.screen.ScreenEvent

sealed interface AccountsEvent : ScreenEvent {

    data object AccountCreated : AccountsEvent
}