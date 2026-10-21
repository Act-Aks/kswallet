package com.kswallet.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

/** Configuration shared by every Android module (app and libraries). */
internal fun Project.configureKotlinAndroid(extension: CommonExtension) {
    with(extension) {
        compileSdk = compileSdkVersion

        defaultConfig.apply {
            minSdk = minSdkVersion
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        compileOptions.apply {
            sourceCompatibility = javaVersion
            targetCompatibility = javaVersion
        }

        lint.apply {
            checkDependencies = true
            abortOnError = true
            warningsAsErrors = false

            // Version nags belong to Renovate-style tooling, not to lint.
            disable.addAll(
                listOf(
                    "GradleDependency",
                    "AndroidGradlePluginVersion",
                    "NewerVersionAvailable",
                ),
            )
        }

        packaging.resources.excludes.addAll(
            listOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "/META-INF/DEPENDENCIES",
                "/META-INF/LICENSE*",
                "/META-INF/NOTICE*",
                "/META-INF/versions/9/OSGI-INF/MANIFEST.MF",
            ),
        )
    }

    // AGP 9 compiles Kotlin itself (built-in Kotlin): there is no kotlin-android
    // plugin and no `kotlinOptions` block anymore - compiler options live on the
    // `kotlin` extension.
    configure<KotlinAndroidProjectExtension> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            freeCompilerArgs.add("-Xjsr305=strict")
        }
    }
}
