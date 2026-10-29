plugins {
    alias(libs.plugins.kswallet.android.library)
    alias(libs.plugins.kswallet.android.lint)
    alias(libs.plugins.kswallet.android.room)
    alias(libs.plugins.kswallet.android.testing)
    alias(libs.plugins.kswallet.kotlin.format)
}

android {
    namespace = "com.kswallet.core.database"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    api(libs.androidx.room3.runtime)
    api(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.sqlite.bundled)

    ksp(libs.androidx.room3.compiler)
}