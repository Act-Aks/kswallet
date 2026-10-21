package com.kswallet

import com.kswallet.convention.applyPlugins
import com.kswallet.convention.configureKotlinJvm
import com.kswallet.convention.configureLayerBoundaryCheck
import com.kswallet.convention.configureUnitTests
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Pure Kotlin/JVM module: the home of domain models and framework-free utilities.
 *
 * No Android, no Compose, no Room, no DI - the layer boundary task fails the build
 * if one of them sneaks in.
 */
class KsWalletKotlinLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        applyPlugins("kotlin-jvm")
        configureKotlinJvm()
        configureUnitTests()
        configureLayerBoundaryCheck()
    }
}
