package com.kswallet.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.kswallet.app.ui.KsWalletApp

/**
 * The app has exactly one Activity.
 *
 * Everything else is a Compose destination, which keeps process-death restoration, deep
 * links and predictive back in one place instead of spread over a dozen screens.
 */
class MainActivity : ComponentActivity() {

    private var keepSplashScreenVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition {
            keepSplashScreenVisible
        }

        setContent {
            KsWalletApp(
                onAppReady = {
                    keepSplashScreenVisible = false
                },
            )
        }
    }
}
