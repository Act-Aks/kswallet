plugins {
    alias(libs.plugins.kswallet.kotlin.library)
    alias(libs.plugins.kswallet.kotlin.format)
}

dependencies {
    api(projects.core.money)
    api(libs.kotlinx.coroutines.core)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)

    testRuntimeOnly(libs.junit.platform.launcher)
}