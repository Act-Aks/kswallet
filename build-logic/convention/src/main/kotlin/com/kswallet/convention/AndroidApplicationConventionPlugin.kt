package com.kswallet.convention

import com.android.build.api.dsl.ApplicationExtension
import com.kswallet.convention.internal.androidTestImplementation
import com.kswallet.convention.internal.applyPlugins
import com.kswallet.convention.internal.configureAndroidApplication
import com.kswallet.convention.internal.configureJunitTestDependencies
import com.kswallet.convention.internal.configureLocalTests
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        applyPlugins("android-application")

        extensions.configure<ApplicationExtension> {
            configureAndroidApplication(this@with)
        }

        configureLocalTests()
        configureJunitTestDependencies()

        androidTestImplementation("junit4")
        androidTestImplementation("androidx-test-runner")
        androidTestImplementation("androidx-test-ext-junit")
    }
}