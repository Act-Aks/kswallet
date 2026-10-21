plugins {
    alias(libs.plugins.kswallet.android.library.compose)
}

android {
    namespace = "com.kswallet.core.ui"
}

dependencies {
    api(projects.core.model)
    api(projects.core.designsystem)
}
