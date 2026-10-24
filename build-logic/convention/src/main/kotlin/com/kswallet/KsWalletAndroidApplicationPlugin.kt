package com.kswallet

import com.android.build.api.dsl.ApplicationExtension
import com.kswallet.convention.appVersion
import com.kswallet.convention.appVersionCode
import com.kswallet.convention.applicationId
import com.kswallet.convention.applyPlugins
import com.kswallet.convention.configureAndroidInstrumentedTests
import com.kswallet.convention.configureCompose
import com.kswallet.convention.configureComposeDependencies
import com.kswallet.convention.configureCoroutines
import com.kswallet.convention.configureKotlinAndroid
import com.kswallet.convention.configureLayerBoundaryCheck
import com.kswallet.convention.configureUnitTests
import com.kswallet.convention.implementation
import com.kswallet.convention.targetSdkVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class KsWalletAndroidApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        applyPlugins(
            "android-application",
            "kswallet-android-di",
        )

        extensions.configure<ApplicationExtension> {
            configureKotlinAndroid(this)
            configureCompose(this)

            testOptions.unitTests.isIncludeAndroidResources = true

            namespace = this@with.applicationId

            defaultConfig {
                applicationId = this@with.applicationId
                versionCode = appVersionCode
                versionName = appVersion
                targetSdk = targetSdkVersion
            }

            buildTypes {
                release {
                    // Keep R8 off until the keep rules in proguard-rules.pro have been
                    // verified against a real release build; then flip this to true.
                    isMinifyEnabled = false
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro",
                    )
                }
                debug {
                    applicationIdSuffix = ".debug"
                }
            }
        }

        implementation("androidx-core-ktx")
        implementation("androidx-core-splashscreen")
        implementation("androidx-lifecycle-viewmodel-navigation3")

        configureCoroutines()
        configureComposeDependencies()
        configureUnitTests()
        configureAndroidInstrumentedTests()
        configureLayerBoundaryCheck(false)
    }
}
