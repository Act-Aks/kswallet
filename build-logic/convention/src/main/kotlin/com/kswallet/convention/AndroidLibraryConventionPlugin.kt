package com.kswallet.convention

import com.android.build.api.dsl.LibraryExtension
import com.kswallet.convention.internal.applyPlugins
import com.kswallet.convention.internal.configureAndroidLibrary
import com.kswallet.convention.internal.configureLocalTests
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        applyPlugins("android-library", "kswallet-android-lint")

        extensions.configure<LibraryExtension> {
            configureAndroidLibrary(this@with)
        }

        configureLocalTests()
    }
}