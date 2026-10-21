plugins {
    alias(libs.plugins.kswallet.android.library.compose)
}

android {
    namespace = "com.kswallet.core.designsystem"
}

dependencies {
    api(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
}
