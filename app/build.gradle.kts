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

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(projects.designSystem.core)

    implementation(projects.core.screen)
    implementation(projects.core.money)
    implementation(projects.core.currencies)
    implementation(projects.core.database)

    implementation(projects.feature.accounts.domain)
    implementation(projects.feature.accounts.data)
    implementation(projects.feature.accounts.presentation)

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

    androidTestImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.ext.junit)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.koin.test)

    testRuntimeOnly(libs.junit.platform.launcher)
}