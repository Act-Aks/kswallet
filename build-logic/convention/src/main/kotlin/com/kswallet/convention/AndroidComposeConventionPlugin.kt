package com.kswallet.convention

import com.kswallet.convention.internal.applyPlugins
import com.kswallet.convention.internal.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        applyPlugins("compose-compiler")
        configureAndroidCompose()
    }
}