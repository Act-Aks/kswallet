package com.kswallet.convention.internal

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configureAndroidCompose() {
    pluginManager.withPlugin("com.android.application") {
        extensions.configure<ApplicationExtension> {
            buildFeatures {
                compose = true
            }
        }
    }

    pluginManager.withPlugin("com.android.library") {
        extensions.configure<LibraryExtension> {
            buildFeatures {
                compose = true
            }
        }
    }
}