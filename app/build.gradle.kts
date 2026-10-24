plugins {
    alias(libs.plugins.kswallet.android.application)
}

android {
    namespace = "com.kswallet.app"
}

/*
 * \:app is the composition root. It may see every module; nothing may see it.
 *
 * The dependency list is deliberately explicit (no `api` leaking from a "shared" module):
 * adding a feature means adding one line here, and the layer-boundary task checks that the
 * app never reaches into a feature's data layer.
 */
dependencies {
    implementation(projects.core.common)
    implementation(projects.core.database)
    implementation(projects.core.datastore)
    implementation(projects.core.designsystem)
    implementation(projects.core.model)
    implementation(projects.core.navigation)
    implementation(projects.core.ui)

    implementation(projects.feature.accounts)
    implementation(projects.feature.budgets)
    implementation(projects.feature.categories)
    implementation(projects.feature.home)
    implementation(projects.feature.onboarding)
    implementation(projects.feature.reports)
    implementation(projects.feature.settings)
    implementation(projects.feature.transactions)
}
