plugins {
    alias(libs.plugins.kswallet.kotlin.library)
}

dependencies {
    api(libs.androidx.lifecycle.viewmodel)
    api(libs.kotlinx.coroutines.core)
}