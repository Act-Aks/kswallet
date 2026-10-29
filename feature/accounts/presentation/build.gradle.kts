plugins {
    alias(libs.plugins.kswallet.android.library)
    alias(libs.plugins.kswallet.android.compose)
    alias(libs.plugins.kswallet.android.testing)
}

android {
    namespace = "com.kswallet.feature.accounts.presentation"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(projects.core.screen)
    implementation(projects.feature.accounts.domain)

    implementation(projects.designSystem.core)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel)

    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.core)
}