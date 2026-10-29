package com.kswallet.convention.internal

import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.withType

internal fun Project.configureLocalTests() {
    tasks.withType<Test>().configureEach {
        useJUnitPlatform()

        testLogging {
            events("failed", "skipped")
        }
    }
}

internal fun Project.configureJunitTestDependencies() {
    testPlatform("junit-bom")
    testImplementation("junit-jupiter")
    testImplementation("junit-jupiter-params")

    testRuntimeOnly("junit-platform-launcher")
}