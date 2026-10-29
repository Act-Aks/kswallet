plugins {
    alias(libs.plugins.kswallet.android.compose)
    alias(libs.plugins.kswallet.android.library)
    alias(libs.plugins.kswallet.android.lint)
    alias(libs.plugins.kswallet.kotlin.format)

}

android {
    namespace = "com.neodesign"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.foundation)

    implementation(libs.androidx.compose.animation.core)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.tooling.preview)

    // Host Activity for createComposeRule().
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    androidTestImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}