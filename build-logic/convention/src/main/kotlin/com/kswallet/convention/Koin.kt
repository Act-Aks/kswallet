package com.kswallet.convention

import org.gradle.api.Project

/**
 * Applies the Koin compiler plugin and the Koin runtime.
 *
 * The compiler plugin resolves the dependency graph at build time, so a missing or
 * mismatched definition fails `assembleDebug` instead of crashing at runtime.
 */
internal fun Project.configureKoin() {
    applyPlugins("koin-compiler")

    koinBom()
    implementation("koin-core")
    implementation("koin-android")
    implementation("koin-annotations")
    implementation("koin-compose")
    implementation("koin-compose-viewmodel")
}
