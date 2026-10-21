package com.kswallet.convention

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

/**
 * Helpers shared by every KsWallet convention plugin.
 *
 * The version catalog is the single source of truth: conventions never hardcode a
 * dependency coordinate or an SDK level, they look it up by alias.
 */
internal val Project.versionCatalog: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun Project.version(alias: String): String = versionCatalog.findVersion(alias)
    .orElseThrow { IllegalStateException("Version '$alias' is missing from gradle/libs.versions.toml") }.requiredVersion

internal fun Project.lib(alias: String): Provider<MinimalExternalModuleDependency> = versionCatalog.findLibrary(alias)
    .orElseThrow { IllegalStateException("Library '$alias' is missing from gradle/libs.versions.toml") }

internal fun Project.pluginId(alias: String): String =
    versionCatalog.findPlugin(alias)
        .orElseThrow { IllegalStateException("Plugin alias '$alias' not found in libs.versions.toml") }
        .get()
        .pluginId

/**
 * Applies multiple plugins by their version-catalog alias (under [plugins] in
 * libs.versions.toml). The alias does NOT need a version (DreamStream convention
 * plugins are versionless).
 */
internal fun Project.applyPlugins(vararg aliases: String): Project {
    with(pluginManager) {
        aliases.forEach { apply(pluginId(it)) }
    }
    return this
}

internal fun Project.add(configuration: String, alias: String) = dependencies.add(configuration, lib(alias).get())

internal fun Project.implementation(alias: String) = add("implementation", alias)
internal fun Project.testImplementation(alias: String) = add("testImplementation", alias)
internal fun Project.debugImplementation(alias: String) = add("debugImplementation", alias)
internal fun Project.androidTestImplementation(alias: String) = add("androidTestImplementation", alias)
internal fun Project.koinBom() = dependencies.add("implementation", dependencies.platform(lib("koin-bom")))

/**
 * Project Metadata Extensions
 */
internal val Project.applicationId: String
    get() = versionCatalog.findVersion("projectApplicationId").get().toString()

internal val Project.applicationName: String
    get() = versionCatalog.findVersion("projectApplicationName").get().toString()

internal val Project.appVersion: String
    get() = versionCatalog.findVersion("projectVersion").get().toString()

internal val Project.appVersionCode: Int
    get() = versionCatalog.findVersion("projectVersionCode").get().toString().toInt()

/**
 * SDK Version Extensions
 */
internal val Project.compileSdkVersion: Int
    get() = versionCatalog.findVersion("projectCompileSdkVersion").get().toString().toInt()

internal val Project.minSdkVersion: Int
    get() = versionCatalog.findVersion("projectMinSdkVersion").get().toString().toInt()

internal val Project.targetSdkVersion: Int
    get() = versionCatalog.findVersion("projectTargetSdkVersion").get().toString().toInt()

/**
 * Java / JVM Version Extensions
 */
internal val Project.projectJavaVersion: Int
    get() = versionCatalog.findVersion("projectJavaVersion").get().toString().toInt()

internal val Project.javaVersion: JavaVersion
    get() = when (projectJavaVersion) {
        17 -> JavaVersion.VERSION_17
        21 -> JavaVersion.VERSION_21
        24 -> JavaVersion.VERSION_24
        else -> JavaVersion.VERSION_17
    }

internal val Project.jvmTarget: JvmTarget
    get() = when (projectJavaVersion) {
        17 -> JvmTarget.JVM_17
        21 -> JvmTarget.JVM_21
        24 -> JvmTarget.JVM_24
        else -> JvmTarget.JVM_17
    }

/**
 * Adds a project dependency by path.
 *
 * Named [projectDep] and not `project` on purpose: `Project.project(String)` is a member of
 * [Project], so a same-named extension would be shadowed by the member and the call would
 * quietly add nothing to the build.
 */
internal fun Project.projectDep(path: String, configuration: String = "implementation") =
    dependencies.add(
        configuration,
        dependencies.project(mapOf("path" to path)),
    )
