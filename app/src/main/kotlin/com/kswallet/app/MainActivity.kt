package com.kswallet.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kswallet.app.ui.KsWalletApp

/**
 * The app has exactly one Activity.
 *
 * Everything else is a Compose destination, which keeps process-death restoration, deep
 * links and predictive back in one place instead of spread over a dozen screens.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KsWalletApp()
        }
    }
}
