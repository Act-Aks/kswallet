plugins {
    alias(libs.plugins.kswallet.kotlin.library)
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.datetime)
}
