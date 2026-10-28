plugins {
    alias(libs.plugins.kswallet.android.library)
    alias(libs.plugins.kswallet.android.compose)
}

android {
    namespace = "com.neodesign"
}

dependencies {
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.ui)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.animation.core)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.tooling.preview)
}