plugins {
    alias(libs.plugins.kswallet.kotlin.library)
}

dependencies {
    api(projects.core.money)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)

    testRuntimeOnly(libs.junit.platform.launcher)
}