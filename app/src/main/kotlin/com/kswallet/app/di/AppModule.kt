package com.kswallet.app.di

import com.kswallet.core.common.time.SystemTimeProvider
import com.kswallet.core.common.time.TimeProvider
import org.koin.dsl.module

/**
 * App-wide definitions that belong to no single feature.
 *
 * `TimeProvider` is a single because "now" must be one clock for the whole process: the
 * ledger's day grouping and the "this month" reports have to agree.
 */
val appModule = module {
    single<TimeProvider> { SystemTimeProvider() }
}
