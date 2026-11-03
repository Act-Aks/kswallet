plugins {
    alias(libs.plugins.kswallet.android.library)
    alias(libs.plugins.kswallet.android.di)
    alias(libs.plugins.kswallet.android.testing)
    alias(libs.plugins.kswallet.android.lint)
    alias(libs.plugins.kswallet.kotlin.format)
}

android {
    namespace = "com.kswallet.feature.transactions.data"
}

dependencies {
    api(projects.feature.transactions.domain)

    implementation(projects.core.database)
    implementation(projects.core.money)

    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.koin.core)
}