package com.kswallet.convention

import com.android.build.api.dsl.ApplicationExtension
import com.kswallet.convention.internal.appVersion
import com.kswallet.convention.internal.appVersionCode
import com.kswallet.convention.internal.applicationId
import com.kswallet.convention.internal.applyPlugins
import com.kswallet.convention.internal.configureAndroidApplication
import com.kswallet.convention.internal.configureLocalTests
import com.kswallet.convention.internal.targetSdkVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        applyPlugins("android-application")

        extensions.configure<ApplicationExtension> {
            configureAndroidApplication(this@with)

            defaultConfig {
                applicationId = this@with.applicationId
                targetSdk = targetSdkVersion
                versionCode = appVersionCode
                versionName = appVersion
            }
        }

        configureLocalTests()
    }
}