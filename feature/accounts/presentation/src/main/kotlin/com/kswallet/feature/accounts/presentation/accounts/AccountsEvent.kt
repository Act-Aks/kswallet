package com.kswallet.feature.accounts.presentation.accounts

import com.kswallet.core.screen.ScreenEvent
import com.kswallet.feature.accounts.domain.model.AccountId

sealed interface AccountsEvent : ScreenEvent {
    data class OpenAccountDetails(val id: AccountId) : AccountsEvent
    data object OpenCreateAccount : AccountsEvent
}