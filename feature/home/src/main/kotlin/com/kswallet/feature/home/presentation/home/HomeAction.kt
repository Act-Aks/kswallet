package com.kswallet.feature.home.presentation.home

import com.kswallet.core.ui.screen.ScreenAction

sealed interface HomeAction : ScreenAction {
    data object AddTransactionClicked : HomeAction
    data object AddAccountClicked : HomeAction
    data object SeeAllTransactionsClicked : HomeAction
    data object SeeAccountsClicked : HomeAction
    data class TransactionClicked(val id: Long) : HomeAction
}
