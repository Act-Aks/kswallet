package com.kswallet

import com.android.build.api.dsl.LibraryExtension
import com.kswallet.convention.applyPlugins
import com.kswallet.convention.configureCompose
import com.kswallet.convention.configureComposeDependencies
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/** Android library that draws Jetpack Compose UI. */
class KsWalletAndroidLibraryComposePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        applyPlugins("kswallet-android-library")

        extensions.configure<LibraryExtension> {
            configureCompose(this)
        }

        configureComposeDependencies()
    }
}
