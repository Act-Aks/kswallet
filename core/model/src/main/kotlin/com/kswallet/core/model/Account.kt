package com.kswallet.core.model

import kotlin.time.Instant

/**
 * A wallet: cash, a bank account, a card, a crypto wallet, ...
 *
 * [balance] is derived state (initial balance + the effect of every transaction), so the
 * database never stores a value that can drift out of sync with the ledger.
 */
data class Account(
    val id: AccountId,
    val name: String,
    val type: AccountType,
    val currency: Currency,
    val balance: Money,
    val initialBalance: Money,
    val iconKey: String,
    val colorArgb: Long,
    val includedInTotal: Boolean,
    val isArchived: Boolean = false,
    val createdAt: Instant,
) {
    val isPersisted: Boolean get() = id.isSaved
}

enum class AccountType {
    CASH,
    BANK,
    CARD,
    WALLET,
    CRYPTO,
    OTHER,
}
