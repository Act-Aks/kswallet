package com.kswallet.convention

import com.kswallet.convention.internal.applyPlugins
import com.kswallet.convention.internal.implementation
import com.kswallet.convention.internal.implementationPlatform
import com.kswallet.convention.internal.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidDiConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        applyPlugins("koin-compiler")

        implementationPlatform("koin-bom")

        implementation("koin-android")

        testImplementation("koin-test")
    }
}