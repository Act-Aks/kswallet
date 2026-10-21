package com.kswallet

import com.kswallet.convention.applyPlugins
import com.kswallet.convention.configureLayerBoundaryCheck
import com.kswallet.convention.implementation
import com.kswallet.convention.projectDep
import com.kswallet.convention.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Convention for every `:feature:*` module: Compose UI + DI + the core modules a
 * feature is allowed to depend on. Features are wired here so that a new feature
 * module is a two-line build file.
 */
class KsWalletAndroidFeaturePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        applyPlugins(
            "kswallet-android-library-compose",
            "kswallet-android-di",
        )

        // Allowed core dependencies - anything else must be added on purpose.
        projectDep(":core:common")
        projectDep(":core:model")
        projectDep(":core:designsystem")
        projectDep(":core:ui")
        projectDep(":core:navigation")
        projectDep(":core:database")
        projectDep(":core:datastore")

        implementation("androidx-datastore-preferences")
        implementation("kotlinx-serialization-json")
        testImplementation("androidx-room3-runtime") // entity/dao types in unit tests

        projectDep(":core:testing", configuration = "testImplementation")

        configureLayerBoundaryCheck(false)
    }
}
