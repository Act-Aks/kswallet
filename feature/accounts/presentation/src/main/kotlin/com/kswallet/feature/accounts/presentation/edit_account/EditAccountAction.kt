package com.kswallet.feature.accounts.presentation.edit_account

import com.kswallet.core.screen.ScreenAction

sealed interface EditAccountAction : ScreenAction {
    data class NameChanged(val value: String) : EditAccountAction
    data object SaveClicked : EditAccountAction
    data object ArchiveToggleClicked : EditAccountAction
}