package com.kswallet.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.withType

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
    implementation("androidx-navigation-compose")

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

    configureUiTests()
}

private fun Project.configureUiTests() {
    tasks.withType<Test>().configureEach {
        // Robolectric's Android runtime plus Compose does not fit in the 512 MB default on a
        // small container. Both knobs are Gradle properties so a memory-starved machine can
        // dial them down (`-Pkswallet.testMaxHeap=448m`) without editing the build logic.
        maxHeapSize = providers.gradleProperty("kswallet.testMaxHeap").getOrElse("640m")

        // Robolectric resolves its `android-all` runtime from Maven the first time a test runs
        // (~200 MB). Pointing it at a directory uses a pre-warmed copy instead - which is what
        // makes a restored sandbox with an empty Maven cache able to run these tests at all.
        // Robolectric reflects into `java.io.FileDescriptor` (and friends) to emulate Android's
        // shared-memory setup. On JDK 17+ those packages are closed by default, and without
        // these the first `AndroidTestEnvironment` boot fails with
        // "Failed to interact with raw FileDescriptor internals; perhaps JRE has changed?".
        jvmArgs(
            "--add-opens=java.base/java.io=ALL-UNNAMED",
            "--add-opens=java.base/java.lang=ALL-UNNAMED",
            "--add-opens=java.base/java.util=ALL-UNNAMED",
            "--add-opens=java.base/java.nio=ALL-UNNAMED",
        )

        val robolectricDeps = providers.gradleProperty("kswallet.robolectricDeps")
            .getOrElse("${System.getProperty("user.home")}/.cache/robolectric-deps")
        if (file(robolectricDeps).isDirectory) {
            systemProperty("robolectric.dependency.dir", robolectricDeps)
        }

        // A Robolectric test needs a JVM of its own next to the Gradle build JVM, and on a
        // memory-starved box (2 GB, no swap, platform services already holding ~500 MB) that is
        // one JVM too many: the kernel OOM-kills the build mid-run and "the Gradle daemon
        // disappeared". Such a machine can run the ordinary suite with
        // `-Pkswallet.skipUiTests=true` and then start these tests itself, one JVM, no Gradle:
        // `tools/run-ui-test.sh` replays the classpath and system properties of this very task
        // (see `tools/dump-test-classpath.init.gradle`).
        if (providers.gradleProperty("kswallet.skipUiTests").orNull == "true") {
            filter { excludeTestsMatching("*ScreenTest") }
        }
    }
}
