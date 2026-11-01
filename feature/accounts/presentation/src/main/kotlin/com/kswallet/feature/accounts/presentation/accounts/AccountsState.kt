package com.kswallet.feature.accounts.presentation.accounts

import com.kswallet.core.screen.ScreenState

data class AccountsState(
    val content: AccountsContent = AccountsContent.Loading,
) : ScreenState