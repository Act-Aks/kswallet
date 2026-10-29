package com.kswallet.convention

import com.kswallet.convention.internal.androidTestImplementation
import com.kswallet.convention.internal.applyPlugins
import com.kswallet.convention.internal.configureJunitTestDependencies
import com.kswallet.convention.internal.configureLocalTests
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidTestingConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        applyPlugins("android-library")

        configureLocalTests()
        configureJunitTestDependencies()

        androidTestImplementation("junit4")
        androidTestImplementation("androidx-test-runner")
        androidTestImplementation("androidx-test-ext-junit")
    }
}