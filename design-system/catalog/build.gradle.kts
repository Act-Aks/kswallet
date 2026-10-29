plugins {
    alias(libs.plugins.kswallet.android.application)
    alias(libs.plugins.kswallet.android.compose)
    alias(libs.plugins.kswallet.android.lint)
    alias(libs.plugins.kswallet.kotlin.format)
}

android {
    namespace = "com.neodesign.catalog"

    defaultConfig {
        applicationId = libs.versions.neoCatalogApplicationId.get()
        targetSdk = libs.versions.projectTargetSdkVersion.get().toInt()
        versionCode = libs.versions.neoCatalogVersionCode.get().toInt()
        versionName = libs.versions.neoCatalogVersion.get()
    }
}

dependencies {
    implementation(projects.designSystem.core)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.runtime.saveable)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
}