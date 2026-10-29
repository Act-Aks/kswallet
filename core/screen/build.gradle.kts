plugins {
    alias(libs.plugins.kswallet.kotlin.library)
    alias(libs.plugins.kswallet.kotlin.testing)
    alias(libs.plugins.kswallet.kotlin.format)
}

dependencies {
    api(libs.androidx.lifecycle.viewmodel)
    api(libs.kotlinx.coroutines.core)
}