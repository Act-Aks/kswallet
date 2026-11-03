package com.kswallet.feature.transactions.data.repository

import com.kswallet.core.database.dao.TransactionDao
import com.kswallet.core.database.entity.TransactionEntity
import com.kswallet.core.identity.AccountId
import com.kswallet.core.identity.TransactionId
import com.kswallet.feature.transactions.data.mapper.toDomain
import com.kswallet.feature.transactions.data.mapper.toEntity
import com.kswallet.feature.transactions.domain.model.NewTransaction
import com.kswallet.feature.transactions.domain.model.Transaction
import com.kswallet.feature.transactions.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepositoryImpl(
    private val dao: TransactionDao,
) : TransactionRepository {

    override fun observeByAccount(accountId: AccountId): Flow<List<Transaction>> =
        dao.observeByAccount(accountId.toString())
            .map { entities ->
                entities.map(TransactionEntity::toDomain)
            }

    override suspend fun findById(id: TransactionId): Transaction? =
        dao.findById(id.toString())?.toDomain()

    override suspend fun create(transaction: NewTransaction): Transaction {
        val entity = transaction.toEntity(TransactionId.new())

        return dao.insertAndRead(entity).toDomain()
    }
}