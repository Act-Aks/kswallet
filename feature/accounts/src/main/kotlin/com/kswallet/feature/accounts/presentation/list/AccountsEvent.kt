package com.kswallet.feature.accounts.presentation.list

import com.kswallet.core.model.AccountId
import com.kswallet.core.ui.screen.ScreenEvent
import com.kswallet.core.ui.util.UiMessage

sealed interface AccountsEvent : ScreenEvent {
    data class OpenAccountDetail(val id: AccountId) : AccountsEvent
    data class OpenEditor(val id: AccountId?) : AccountsEvent
    data class ShowMessage(val message: UiMessage) : AccountsEvent
}
