package com.kswallet.convention.internal

import androidx.room3.gradle.RoomExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configureRoom() {
    val schemaPath = layout.projectDirectory
        .dir("schemas")
        .asFile
        .absolutePath

    extensions.configure<RoomExtension> {
        schemaDirectory(schemaPath)
    }
}