plugins {
    alias(libs.plugins.kswallet.android.library)
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

    androidTestImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.ext.junit)
}