package com.kswallet.app

import android.app.Application
import com.kswallet.app.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * The DI entry point of the app.
 *
 * Starting Koin here (and nowhere else) is what makes the dependency graph verifiable at
 * build time: the Koin compiler plugin reads the modules passed to [startKoin] - including
 * the ones nested in `listOf(...)`, which is why [appModules] is built from those lists -
 * and fails `assembleDebug` when a definition is missing, instead of crashing the first
 * time a screen asks for it.
 */
class KsWalletApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@KsWalletApplication)
            modules(appModules)
        }
    }
}
