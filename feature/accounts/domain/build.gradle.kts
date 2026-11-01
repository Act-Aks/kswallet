plugins {
    alias(libs.plugins.kswallet.kotlin.format)
    alias(libs.plugins.kswallet.kotlin.library)
    alias(libs.plugins.kswallet.kotlin.testing)
}

dependencies {
    api(projects.core.identity)
    api(projects.core.money)
    api(libs.kotlinx.coroutines.core)
}