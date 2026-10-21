package com.kswallet.convention

import org.gradle.api.Project

/** Coroutines are infrastructure: every module gets them. */
internal fun Project.configureCoroutines() {
    implementation("kotlinx-coroutines-core")
    implementation("kotlinx-coroutines-android")
}
