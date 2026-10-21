plugins {
    alias(libs.plugins.kswallet.android.library.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.kswallet.core.navigation"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}
