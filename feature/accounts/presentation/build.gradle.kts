plugins {
    alias(libs.plugins.kswallet.android.library)
}

android {
    namespace = "com.kswallet.feature.accounts.presentation"
}

dependencies {
    implementation(projects.core.screen)
    implementation(projects.core.money)

    implementation(projects.feature.accounts.domain)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.lifecycle.viewmodel)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotlinx.coroutines.test)

    testRuntimeOnly(libs.junit.platform.launcher)
}