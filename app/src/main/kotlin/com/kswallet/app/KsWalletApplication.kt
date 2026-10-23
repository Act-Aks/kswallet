package com.kswallet.app

import android.app.Application
import com.kswallet.app.di.KsWalletKoinApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level
import org.koin.plugin.module.dsl.startKoin

/** Application entry point. The annotated Koin graph is compile-time checked. */
class KsWalletApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin<KsWalletKoinApplication> {
            androidLogger(Level.ERROR)
            androidContext(this@KsWalletApplication)
        }
    }
}
