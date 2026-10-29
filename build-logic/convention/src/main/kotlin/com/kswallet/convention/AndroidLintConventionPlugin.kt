package com.kswallet.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.Lint
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLintConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        pluginManager.withPlugin("com.android.library") {
            extensions.configure<LibraryExtension> {
                lint {
                    configureSharedLint()
                }
            }
        }

        pluginManager.withPlugin("com.android.application") {
            extensions.configure<ApplicationExtension> {
                lint {
                    configureSharedLint()
                }
            }
        }
    }
}

private fun Lint.configureSharedLint() {
    abortOnError = true
    warningsAsErrors = false

    disable += listOf(
        "GradleDependency",
        "NewerVersionAvailable",
    )
}