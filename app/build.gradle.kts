plugins {
    alias(libs.plugins.kswallet.android.application)
}

android {
    namespace = "com.kswallet.app"
}

/*
 * :app is the composition root. It may see every module; nothing may see it.
 *
 * The dependency list is deliberately explicit (no `api` leaking from a "shared" module):
 * adding a feature means adding one line here, and the layer-boundary task checks that the
 * app never reaches into a feature's data layer.
 */
dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:ui"))
    implementation(project(":core:navigation"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))

    implementation(project(":feature:home"))
    implementation(project(":feature:accounts"))
    implementation(project(":feature:transactions"))
    implementation(project(":feature:budgets"))
    implementation(project(":feature:reports"))
    implementation(project(":feature:categories"))
    implementation(project(":feature:settings"))
}
