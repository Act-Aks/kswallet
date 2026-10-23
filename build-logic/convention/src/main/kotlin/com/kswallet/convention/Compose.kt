package com.kswallet.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project

/** Compose is enabled through AGP's build feature + the Kotlin Compose compiler plugin. */
internal fun Project.configureCompose(extension: CommonExtension) {
    applyPlugins("kotlin-compose")
    extension.buildFeatures.compose = true
}

/** Compose, lifecycle and tooling dependencies shared by all Compose modules. */
internal fun Project.configureComposeDependencies() {
    dependencies.add(
        "implementation",
        dependencies.platform(lib("androidx-compose-bom")),
    )
    implementation("androidx-compose-ui")
    implementation("androidx-compose-ui-graphics")
    implementation("androidx-compose-ui-tooling-preview")
    implementation("androidx-compose-foundation")
    implementation("androidx-compose-material3")
    implementation("androidx-compose-material-icons-core")
    implementation("androidx-lifecycle-runtime-compose")
    implementation("androidx-lifecycle-viewmodel-compose")
    implementation("androidx-navigation3-runtime")
    implementation("androidx-navigation3-ui")

    debugImplementation("androidx-compose-ui-tooling")

    dependencies.add(
        "androidTestImplementation",
        dependencies.platform(lib("androidx-compose-bom")),
    )
    androidTestImplementation("androidx-compose-ui-test-junit4")
    debugImplementation("androidx-compose-ui-test-manifest")

    // The same Compose UI tests, but hosted on the JVM by Robolectric and executed by
    // `testDebugUnitTest`. This is the difference between "a UI test exists" and "a UI test
    // runs": an `androidTest` needs an emulator, and neither CI nor the machine this project
    // is built on has one, so the assertions would never execute. Robolectric boots a real
    // Android runtime (its `android-all` jar, downloaded on first use) inside the test worker,
    // which makes `@RunWith(AndroidJUnit4::class)` Compose tests part of the normal test task.
    //
    // `maxHeapSize` is raised because that runtime plus Compose's own classloading does not fit
    // in the 512 MB default on a memory-starved container.
    dependencies.add(
        "testImplementation",
        dependencies.platform(lib("androidx-compose-bom")),
    )
    testImplementation("androidx-compose-ui-test-junit4")
    testImplementation("androidx-test-ext-junit")
    testImplementation("robolectric")
}
