package com.kswallet.convention.internal

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

/**
 * Helpers shared by every KsWallet convention plugin.
 *
 * The version catalog is the single source of truth: conventions never hardcode a
 * dependency coordinate or an SDK level, they look it up by alias.
 */
internal val Project.versionCatalog: VersionCatalog
    get() = extensions
        .getByType<VersionCatalogsExtension>()
        .named("libs")

internal fun Project.version(alias: String): String {
    val constraint = versionCatalog.findVersion(alias)
        .orElseThrow {
            GradleException(
                "Version '$alias' is missing from the 'libs' version catalog.",
            )
        }

    return constraint.requiredVersion.takeIf(String::isNotBlank)
        ?: throw GradleException(
            "Version '$alias' must declare a required version.",
        )
}

internal fun Project.intVersion(alias: String): Int {
    val value = version(alias)

    return value.toIntOrNull()
        ?: throw GradleException(
            "Version '$alias' must be an integer, but was '$value'.",
        )
}

internal fun Project.lib(
    alias: String,
): Provider<MinimalExternalModuleDependency> =
    versionCatalog.findLibrary(alias)
        .orElseThrow {
            GradleException(
                "Library '$alias' is missing from the 'libs' version catalog.",
            )
        }

internal fun Project.pluginId(alias: String): String =
    versionCatalog.findPlugin(alias)
        .orElseThrow {
            GradleException(
                "Plugin '$alias' is missing from the 'libs' version catalog.",
            )
        }
        .get()
        .pluginId

/**
 * Applies plugins already available on the build's plugin classpath.
 *
 * Reading an alias here does not resolve or enforce its plugin version.
 */
internal fun Project.applyPlugins(vararg aliases: String) {
    aliases.forEach { alias ->
        pluginManager.apply(pluginId(alias))
    }
}

internal fun Project.add(
    configuration: String,
    alias: String,
) {
    dependencies.add(configuration, lib(alias))
}

internal fun Project.implementation(alias: String) {
    add("implementation", alias)
}

internal fun Project.testImplementation(alias: String) {
    add("testImplementation", alias)
}

internal fun Project.testRuntimeOnly(alias: String) {
    add("testRuntimeOnly", alias)
}

internal fun Project.debugImplementation(alias: String) {
    add("debugImplementation", alias)
}

internal fun Project.androidTestImplementation(alias: String) {
    add("androidTestImplementation", alias)
}

internal fun Project.projectDep(
    path: String,
    configuration: String = "implementation",
) {
    dependencies.add(
        configuration,
        dependencies.project(mapOf("path" to path)),
    )
}