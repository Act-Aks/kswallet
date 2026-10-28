package com.kswallet.app

import android.app.Application
import com.kswallet.app.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KsWalletApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@KsWalletApplication)
            modules(appModules)
        }
    }
}