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
    implementation(libs.compose.compiler.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)

    implementation(libs.ksp.gradle.plugin)
    implementation(libs.room3.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "kswallet.android.application"
            implementationClass = "com.kswallet.convention.AndroidApplicationConventionPlugin"
        }

        register("androidCompose") {
            id = "kswallet.android.compose"
            implementationClass =
                "com.kswallet.convention.AndroidComposeConventionPlugin"
        }

        register("androidLibrary") {
            id = "kswallet.android.library"
            implementationClass = "com.kswallet.convention.AndroidLibraryConventionPlugin"
        }

        register("androidRoom") {
            id = "kswallet.android.room"
            implementationClass =
                "com.kswallet.convention.AndroidRoomConventionPlugin"
        }

        register("kotlinLibrary") {
            id = "kswallet.kotlin.library"
            implementationClass = "com.kswallet.convention.KotlinLibraryConventionPlugin"
        }
    }
}