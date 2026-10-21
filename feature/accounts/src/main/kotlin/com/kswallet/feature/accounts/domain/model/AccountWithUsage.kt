package com.kswallet.feature.accounts.domain.model

import com.kswallet.core.model.Account

/**
 * An account plus how much history it carries, so the delete confirmation can say
 * "3 transactions will be deleted too" instead of a vague warning.
 */
data class AccountWithUsage(
    val account: Account,
    val transactionCount: Int,
)
