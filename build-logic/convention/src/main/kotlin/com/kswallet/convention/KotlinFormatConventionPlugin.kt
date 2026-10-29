package com.kswallet.convention

import com.kswallet.convention.internal.applyPlugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

class KotlinFormatConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        applyPlugins("ktlint")

        extensions.configure<KtlintExtension> {
            reporters {
                reporter(ReporterType.PLAIN)
                reporter(ReporterType.HTML)
            }

            filter {
                exclude("**/build/**")
                exclude("**/generated/**")
            }
        }
    }
}