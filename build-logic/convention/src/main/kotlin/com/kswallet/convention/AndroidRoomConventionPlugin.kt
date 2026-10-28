package com.kswallet.convention

import com.kswallet.convention.internal.applyPlugins
import com.kswallet.convention.internal.configureRoom
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidRoomConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        applyPlugins("ksp", "room3")

        configureRoom()
    }
}