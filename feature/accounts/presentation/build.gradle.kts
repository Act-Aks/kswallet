plugins {
    alias(libs.plugins.kswallet.android.library)
    alias(libs.plugins.kswallet.android.compose)
    alias(libs.plugins.kswallet.android.compose.di)
    alias(libs.plugins.kswallet.android.lint)
    alias(libs.plugins.kswallet.android.testing)
    alias(libs.plugins.kswallet.kotlin.format)
}

android {
    namespace = "com.kswallet.feature.accounts.presentation"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(projects.core.screen)
    implementation(projects.core.navigation)
    implementation(projects.designSystem.core)
    implementation(projects.feature.accounts.domain)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel)

    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    testImplementation(libs.kotlinx.coroutines.test)
}