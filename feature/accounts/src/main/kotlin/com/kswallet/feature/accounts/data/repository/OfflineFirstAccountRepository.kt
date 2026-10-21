package com.kswallet.feature.accounts.data.repository

import com.kswallet.core.common.result.Outcome
import com.kswallet.core.common.result.outcomeOfSuspend
import com.kswallet.core.database.dao.AccountDao
import com.kswallet.core.model.Account
import com.kswallet.core.model.AccountId
import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import com.kswallet.feature.accounts.data.mapper.toDomain
import com.kswallet.feature.accounts.data.mapper.toEntity
import com.kswallet.feature.accounts.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class OfflineFirstAccountRepository(
    private val accountDao: AccountDao,
) : AccountRepository {

    override fun observeAccounts(): Flow<List<Account>> =
        accountDao.observeAccountsWithBalance().map { rows -> rows.map { it.toDomain() } }

    override fun observeAccount(accountId: AccountId): Flow<Account?> =
        accountDao.observeAccountWithBalance(accountId.value).map { row -> row?.toDomain() }

    override fun observeTotalBalance(currency: Currency): Flow<Money> =
        accountDao.observeTotalBalance(currency.code).map { Money(it, currency) }

    override fun observeForeignCurrencyAccountCount(baseCurrency: Currency): Flow<Int> =
        accountDao.observeAccountsWithBalance().map { rows ->
            rows.count { row -> !row.account.currencyCode.equals(baseCurrency.code, ignoreCase = true) }
        }

    override suspend fun getAccount(accountId: AccountId): Outcome<Account> = outcomeOfSuspend {
        // Reading the current balance is a query, so it goes through the same SQL the list
        // uses - one definition of "balance" for the whole app.
        val row = accountDao.observeAccountWithBalance(accountId.value).first()
            ?: throw NoSuchElementException("Account ${accountId.value} not found")
        row.toDomain()
    }

    override suspend fun upsertAccount(account: Account): Outcome<AccountId> = outcomeOfSuspend {
        if (account.id.isSaved) {
            accountDao.update(account.toEntity())
            account.id
        } else {
            AccountId(accountDao.insert(account.toEntity()))
        }
    }

    override suspend fun deleteAccount(accountId: AccountId): Outcome<Unit> = outcomeOfSuspend {
        val entity = accountDao.findById(accountId.value)
            ?: throw NoSuchElementException("Account ${accountId.value} not found")
        accountDao.delete(entity)
    }

    override suspend fun countTransactions(accountId: AccountId): Outcome<Int> =
        outcomeOfSuspend { accountDao.countTransactionsFor(accountId.value) }

    override suspend fun countAccounts(): Outcome<Int> = outcomeOfSuspend { accountDao.countAccounts() }
}
