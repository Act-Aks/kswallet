package com.kswallet.feature.reports.data.di

import com.kswallet.feature.reports.data.repository.OfflineFirstReportRepository
import com.kswallet.feature.reports.domain.repository.ReportRepository
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

val reportsDataModule = module {
    single<OfflineFirstReportRepository>() bind ReportRepository::class
}
