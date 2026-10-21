plugins {
    alias(libs.plugins.kswallet.android.library)
    alias(libs.plugins.kswallet.android.di)
}

android {
    namespace = "com.kswallet.core.datastore"
}

dependencies {
    api(projects.core.model)
    implementation(libs.androidx.datastore.preferences)
}
