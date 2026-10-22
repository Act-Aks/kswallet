package com.kswallet.feature.transactions.domain.model

import com.kswallet.core.model.AccountId
import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.TransactionType

/**
 * What the list screen filters on.
 *
 * The filter is a plain value object: it lives in the state, the user changes it with
 * actions, and the use case turns it into query parameters. No filter logic in the UI.
 */
data class TransactionFilter(
    val type: TransactionType? = null,
    val accountId: AccountId? = null,
    val categoryId: CategoryId? = null,
    val period: PeriodOption = PeriodOption.THIS_MONTH,
)
