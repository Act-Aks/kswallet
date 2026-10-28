plugins {
    `kotlin-dsl`
}

group = "com.kswallet.buildlogic"

kotlin {
    jvmToolchain(
        libs.versions.projectJavaVersion.get().toInt(),
    )
}

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "kswallet.android.application"
            implementationClass = "com.kswallet.convention.AndroidApplicationConventionPlugin"
        }

        register("androidLibrary") {
            id = "kswallet.android.library"
            implementationClass = "com.kswallet.convention.AndroidLibraryConventionPlugin"
        }

        register("kotlinLibrary") {
            id = "kswallet.kotlin.library"
            implementationClass = "com.kswallet.convention.KotlinLibraryConventionPlugin"
        }
    }
}