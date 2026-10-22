package com.kswallet.feature.transactions.presentation.list

import com.kswallet.core.model.TransactionId
import com.kswallet.core.model.TransactionType
import com.kswallet.core.ui.screen.ScreenAction
import com.kswallet.feature.transactions.domain.model.PeriodOption

sealed interface TransactionsAction : ScreenAction {
    /** `null` means "every type". */
    data class TypeFilterSelected(val type: TransactionType?) : TransactionsAction
    data class PeriodSelected(val period: PeriodOption) : TransactionsAction
    data class TransactionClicked(val id: TransactionId) : TransactionsAction
    data object AddTransactionClicked : TransactionsAction
    data class DeleteClicked(val id: TransactionId) : TransactionsAction
    data object DeleteConfirmed : TransactionsAction
    data object DeleteDismissed : TransactionsAction
}
