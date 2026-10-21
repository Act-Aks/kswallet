plugins {
    alias(libs.plugins.kswallet.android.feature)
}

android {
    namespace = "com.kswallet.feature.home"
}

dependencies {
    // Home is an aggregator: it reads the accounts and transactions *domain* layers and
    // renders a dashboard. It never touches their data or presentation code.
    implementation(project(":feature:accounts"))
    implementation(project(":feature:transactions"))
}
