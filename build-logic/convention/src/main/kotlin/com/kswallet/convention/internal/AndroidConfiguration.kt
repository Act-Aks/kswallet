package com.kswallet.convention.internal

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project

internal fun ApplicationExtension.configureAndroidApplication(
    project: Project,
) {
    configureSdk(project)
    configureDefaultConfig(project)
    configureCompileOptions(project)
}

internal fun LibraryExtension.configureAndroidLibrary(
    project: Project,
) {
    configureSdk(project)
    configureDefaultConfig(project)
    configureCompileOptions(project)
}

private fun ApplicationExtension.configureSdk(project: Project) {
    compileSdk = project.compileSdkVersion
}

private fun LibraryExtension.configureSdk(project: Project) {
    compileSdk = project.compileSdkVersion
}

private fun ApplicationExtension.configureDefaultConfig(project: Project) {
    defaultConfig {
        minSdk = project.minSdkVersion
    }
}

private fun LibraryExtension.configureDefaultConfig(project: Project) {
    defaultConfig {
        minSdk = project.minSdkVersion
    }
}

private fun ApplicationExtension.configureCompileOptions(project: Project) {
    compileOptions {
        sourceCompatibility = project.javaVersion
        targetCompatibility = project.javaVersion
    }
}

private fun LibraryExtension.configureCompileOptions(project: Project) {
    compileOptions {
        sourceCompatibility = project.javaVersion
        targetCompatibility = project.javaVersion
    }
}