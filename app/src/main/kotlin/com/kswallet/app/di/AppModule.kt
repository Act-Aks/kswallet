package com.kswallet.app.di

import com.kswallet.core.common.time.SystemTimeProvider
import com.kswallet.core.common.time.TimeProvider
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

/** App-wide definition that belongs to no single feature. */
@Module
class AppModule {
    /** One process-wide clock keeps ledger and report date boundaries consistent. */
    @Single
    fun provideTimeProvider(): TimeProvider = SystemTimeProvider()
}
