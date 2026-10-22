plugins {
    alias(libs.plugins.kswallet.android.feature)
}

android {
    namespace = "com.kswallet.feature.transactions"
}

dependencies {
    // Cross-feature dependencies are allowed on the *domain* layer only: the transactions
    // editor needs the accounts and categories catalogs, not their screens. The build
    // enforces this (see verifyLayerBoundaries).
    implementation(projects.feature.accounts)
    implementation(projects.feature.categories)
}
