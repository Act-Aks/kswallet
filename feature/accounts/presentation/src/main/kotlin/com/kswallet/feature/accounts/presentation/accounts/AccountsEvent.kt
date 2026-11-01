package com.kswallet.feature.accounts.presentation.accounts

import com.kswallet.core.screen.ScreenEvent

sealed interface AccountsEvent : ScreenEvent {
    data class OpenAccountDetails(val id: String) : AccountsEvent
    data object OpenCreateAccount : AccountsEvent
}