package com.kswallet.convention.internal

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

internal val Project.compileSdkVersion: Int
    get() = intVersion("projectCompileSdkVersion")

internal val Project.minSdkVersion: Int
    get() = intVersion("projectMinSdkVersion")

internal val Project.targetSdkVersion: Int
    get() = intVersion("projectTargetSdkVersion")

internal val Project.projectJavaVersion: Int
    get() = intVersion("projectJavaVersion")

internal val Project.javaVersion: JavaVersion
    get() = JavaVersion.toVersion(projectJavaVersion)

internal val Project.jvmTarget: JvmTarget
    get() = JvmTarget.fromTarget(projectJavaVersion.toString())

internal val Project.applicationId: String
    get() = version("projectApplicationId")

internal val Project.applicationName: String
    get() = version("projectApplicationName")

internal val Project.appVersion: String
    get() = version("projectVersion")

internal val Project.appVersionCode: Int
    get() = intVersion("projectVersionCode")