plugins {
    alias(libs.plugins.kswallet.android.compose)
    alias(libs.plugins.kswallet.android.library)
    alias(libs.plugins.kswallet.android.lint)
    alias(libs.plugins.kswallet.android.testing)
    alias(libs.plugins.kswallet.kotlin.format)
}

android {
    namespace = "com.kswallet.core.navigation"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    api(libs.androidx.navigation3.runtime)

    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.kotlinx.serialization.core)
}