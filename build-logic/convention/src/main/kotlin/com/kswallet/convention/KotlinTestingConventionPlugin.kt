package com.kswallet.convention

import com.kswallet.convention.internal.applyPlugins
import com.kswallet.convention.internal.configureJunitTestDependencies
import com.kswallet.convention.internal.configureLocalTests
import org.gradle.api.Plugin
import org.gradle.api.Project

class KotlinTestingConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        applyPlugins("kotlin-jvm")

        configureLocalTests()
        configureJunitTestDependencies()
    }
}