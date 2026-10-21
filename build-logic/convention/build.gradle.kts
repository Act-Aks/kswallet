plugins {
    `kotlin-dsl`
}

group = "com.kswallet.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

dependencies {
    // Gradle-plugin APIs are only needed to compile the convention plugins.
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)

    // Plugins that the conventions apply() at runtime must be on the runtime classpath.
    implementation(libs.koin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("kswalletKotlinLibrary") {
            id = "kswallet.kotlin.library"
            implementationClass = "com.kswallet.KsWalletKotlinLibraryPlugin"
        }
        register("kswalletAndroidLibrary") {
            id = "kswallet.android.library"
            implementationClass = "com.kswallet.KsWalletAndroidLibraryPlugin"
        }
        register("kswalletAndroidLibraryCompose") {
            id = "kswallet.android.library.compose"
            implementationClass = "com.kswallet.KsWalletAndroidLibraryComposePlugin"
        }
        register("kswalletAndroidDi") {
            id = "kswallet.android.di"
            implementationClass = "com.kswallet.KsWalletAndroidDiPlugin"
        }
        register("kswalletAndroidFeature") {
            id = "kswallet.android.feature"
            implementationClass = "com.kswallet.KsWalletAndroidFeaturePlugin"
        }
        register("kswalletAndroidApplication") {
            id = "kswallet.android.application"
            implementationClass = "com.kswallet.KsWalletAndroidApplicationPlugin"
        }
    }
}
