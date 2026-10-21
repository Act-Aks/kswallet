package com.kswallet.feature.accounts.domain.repository

import com.kswallet.core.common.result.Outcome
import com.kswallet.core.model.Account
import com.kswallet.core.model.AccountId
import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import kotlinx.coroutines.flow.Flow

/**
 * What the accounts domain needs from storage - and nothing more.
 *
 * Balances are never written: they are derived from the initial balance plus the ledger, so
 * a balance cannot drift out of sync with the transactions that produced it.
 */
interface AccountRepository {
    fun observeAccounts(): Flow<List<Account>>

    fun observeAccount(accountId: AccountId): Flow<Account?>

    /** Sum of every account included in the total, in [currency]. */
    fun observeTotalBalance(currency: Currency): Flow<Money>

    /** Count of accounts held in a currency other than [baseCurrency] (excluded from the total). */
    fun observeForeignCurrencyAccountCount(baseCurrency: Currency): Flow<Int>

    suspend fun getAccount(accountId: AccountId): Outcome<Account>

    suspend fun upsertAccount(account: Account): Outcome<AccountId>

    suspend fun deleteAccount(accountId: AccountId): Outcome<Unit>

    suspend fun countTransactions(accountId: AccountId): Outcome<Int>

    suspend fun countAccounts(): Outcome<Int>
}
