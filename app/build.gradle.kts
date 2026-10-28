plugins {
    alias(libs.plugins.kswallet.android.application)
    alias(libs.plugins.kswallet.android.compose)
}

android {
    namespace = "com.kswallet.app"

    defaultConfig {
        applicationId = libs.versions.projectApplicationId.get()
        targetSdk = libs.versions.projectTargetSdkVersion.get().toInt()
        versionCode = libs.versions.projectVersionCode.get().toInt()
        versionName = libs.versions.projectVersion.get()
    }
}

dependencies {
    implementation(projects.designSystem.core)
    implementation(projects.core.screen)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.core)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotlinx.coroutines.test)

    testRuntimeOnly(libs.junit.platform.launcher)
}