package com.kswallet.convention

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

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

internal fun Project.configureUnitTests() {
    testImplementation("junit4")
    testImplementation("kotlin-test")
    testImplementation("kotlinx-coroutines-test")
    testImplementation("turbine")
    testImplementation("mockk")
}

internal fun Project.configureAndroidInstrumentedTests() {
    androidTestImplementation("androidx-test-ext-junit")
    androidTestImplementation("androidx-test-espresso-core")
}
