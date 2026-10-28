plugins {
    alias(libs.plugins.kswallet.android.application)
    alias(libs.plugins.kswallet.android.compose)
}

android {
    namespace = "com.kswallet.app"
}

dependencies {
    implementation(projects.designSystem.core)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
}