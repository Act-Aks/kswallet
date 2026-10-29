plugins {
    alias(libs.plugins.kswallet.android.library)
    alias(libs.plugins.kswallet.android.testing)
    alias(libs.plugins.kswallet.android.lint)
    alias(libs.plugins.kswallet.kotlin.format)
}

android {
    namespace = "com.kswallet.feature.accounts.data"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    api(projects.feature.accounts.domain)

    implementation(projects.core.database)
    implementation(projects.core.money)

    implementation(libs.kotlinx.coroutines.core)
}