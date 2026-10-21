package com.kswallet

import com.android.build.api.dsl.LibraryExtension
import com.kswallet.convention.applyPlugins
import com.kswallet.convention.configureAndroidInstrumentedTests
import com.kswallet.convention.configureAndroidUnitTestResources
import com.kswallet.convention.configureCoroutines
import com.kswallet.convention.configureKotlinAndroid
import com.kswallet.convention.configureLayerBoundaryCheck
import com.kswallet.convention.configureUnitTests
import com.kswallet.convention.implementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/** Base Android library convention: SDK levels, Java/Kotlin targets, lint, tests. */
class KsWalletAndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        applyPlugins("android-library")

        extensions.configure<LibraryExtension> {
            configureKotlinAndroid(this)
        }

        implementation("androidx-core-ktx")

        configureCoroutines()
        configureAndroidUnitTestResources()
        configureUnitTests()
        configureAndroidInstrumentedTests()
        configureLayerBoundaryCheck(false)
    }
}
