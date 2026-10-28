package com.kswallet.feature.accounts.data.repository

import com.kswallet.core.database.account.AccountDao
import com.kswallet.core.money.CurrencyRegistry
import com.kswallet.core.money.CurrencyUnit
import com.kswallet.feature.accounts.data.error.AccountCurrencyRejectedException
import com.kswallet.feature.accounts.data.mapper.toDomain
import com.kswallet.feature.accounts.data.mapper.toEntity
import com.kswallet.feature.accounts.domain.model.Account
import com.kswallet.feature.accounts.domain.model.AccountId
import com.kswallet.feature.accounts.domain.model.AccountName
import com.kswallet.feature.accounts.domain.model.AccountStatus
import com.kswallet.feature.accounts.domain.model.NewAccount
import com.kswallet.feature.accounts.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomAccountRepository(
    private val accountDao: AccountDao,
    private val currencyRegistry: CurrencyRegistry,
) : AccountRepository {

    override fun observeAccounts(): Flow<List<Account>> =
        accountDao.observeAll().map { entities ->
            entities.map { entity ->
                entity.toDomain()
            }
        }

    override suspend fun findById(id: AccountId): Account? =
        accountDao.findById(id.toString())?.toDomain()

    override suspend fun create(account: NewAccount): Account {
        val currency = validatedCurrency(account.currency)

        val created = Account(
            id = AccountId.new(),
            name = account.name,
            currency = currency,
            status = AccountStatus.Active,
        )

        accountDao.insert(created.toEntity())

        return created
    }

    override suspend fun rename(
        id: AccountId,
        name: AccountName,
    ): Account? =
        accountDao.renameAndRead(
            id = id.toString(),
            name = name.value,
        )?.toDomain()

    override suspend fun setStatus(
        id: AccountId,
        status: AccountStatus,
    ): Account? =
        accountDao.setArchivedAndRead(
            id = id.toString(),
            archived = status == AccountStatus.Archived,
        )?.toDomain()

    private fun validatedCurrency(
        supplied: CurrencyUnit,
    ): CurrencyUnit {
        val registered = currencyRegistry.find(supplied.code)

        if (
            registered == null ||
            registered.minorUnitScale != supplied.minorUnitScale
        ) {
            throw AccountCurrencyRejectedException(
                code = supplied.code,
                suppliedScale = supplied.minorUnitScale,
                registeredScale = registered?.minorUnitScale,
            )
        }

        return registered
    }
}