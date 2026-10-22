package com.kswallet.feature.transactions.data.di

import com.kswallet.feature.transactions.data.repository.OfflineFirstTransactionRepository
import com.kswallet.feature.transactions.domain.repository.TransactionRepository
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

val transactionsDataModule = module {
    single<OfflineFirstTransactionRepository>() bind TransactionRepository::class
}
