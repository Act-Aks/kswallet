plugins {
    alias(libs.plugins.kswallet.android.compose)
    alias(libs.plugins.kswallet.android.library)
    alias(libs.plugins.kswallet.android.testing)
    alias(libs.plugins.kswallet.kotlin.format)
}

android {
    namespace = "com.kswallet.core.screen"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    api(libs.androidx.lifecycle.viewmodel)
    api(libs.kotlinx.coroutines.core)

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.compose.runtime)
}