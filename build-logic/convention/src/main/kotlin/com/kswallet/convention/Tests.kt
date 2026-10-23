package com.kswallet.convention

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType

/** JVM unit-test stack every module is wired without of the box. */
/**
 * Makes the module's own resources (strings, dimensions) readable from a JVM test.
 *
 * Robolectric asserts on what the user sees, and what the user sees is a string resource; the
 * tests call `context.getString(R.string.x)` rather than hard-coding English, so the merged
 * resources have to be on the unit-test classpath.
 */
internal fun Project.configureAndroidUnitTestResources() {
    extensions.configure<LibraryExtension> {
        testOptions.unitTests.isIncludeAndroidResources = true
    }
}

internal fun Project.configureAndroidInstrumentedTests() {
    androidTestImplementation("androidx-test-ext-junit")
    androidTestImplementation("androidx-test-espresso-core")
}

/** JVM unit-test stack every module is wired with out of the box. */
internal fun Project.configureUnitTests() {
    // JUnit 6: Platform, Jupiter and Vintage share one version number, so the
    // BOM pins them together. The unit tests are Jupiter tests.
    dependencies.add(
        "testImplementation",
        dependencies.platform(lib("junit-bom")),
    )
    testImplementation("junit-jupiter")
    // The Compose screen tests are still JUnit 4 tests - Compose ships its
    // test rule only as `ui-test-junit4` - so the platform needs the vintage
    // engine to discover them when they are run through Gradle on a machine
    // with the memory for it.
    dependencies.add(
        "testRuntimeOnly",
        lib("junit-vintage-engine"),
    )
    // Gradle's `useJUnitPlatform()` needs the launcher on the runtime classpath.
    dependencies.add(
        "testRuntimeOnly",
        lib("junit-platform-launcher"),
    )
    testImplementation("kotlinx-coroutines-test")
    testImplementation("turbine")
    testImplementation("mockk")

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()

        // Robolectric's Android runtime plus Compose does not fit in the 512 MB default on a
        // small container. Both knobs are Gradle properties so a memory-starved machine can
        // dial them down (`-Pkswallet.testMaxHeap=448m`) without editing the build logic.
        maxHeapSize = providers.gradleProperty("kswallet.testMaxHeap").getOrElse("640m")

        // Robolectric resolves its `android-all` runtime from Maven the first time a test runs
        // (~200 MB). Pointing it at a directory uses a pre-warmed copy instead - which is what
        // makes a restored sandbox with an empty Maven cache able to run these tests at all.
        val robolectricDeps = providers.gradleProperty("kswallet.robolectricDeps")
            .getOrElse("${System.getProperty("user.home")}/.cache/robolectric-deps")
        if (file(robolectricDeps).isDirectory) {
            systemProperty("robolectric.dependency.dir", robolectricDeps)
        }

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
