package com.kswallet.feature.accounts.presentation.list

import com.kswallet.core.model.Money
import com.kswallet.core.ui.screen.ScreenState

data class AccountsState(
    val isLoading: Boolean = true,
    val accounts: List<AccountCardUi> = emptyList(),
    val totalBalance: Money? = null,
    val hiddenForeignCurrencyAccounts: Int = 0,
    val pendingDelete: PendingAccountDelete? = null,
) : ScreenState {
    val isEmpty: Boolean get() = !isLoading && accounts.isEmpty()
}
