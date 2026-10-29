plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.lint) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.room3) apply false
}

tasks.register<Delete>("clean") {
    description = "Cleans the root build directory only."
    delete(layout.buildDirectory)
}

tasks.register("formatKotlin") {
    group = "formatting"
    description = "Formats Kotlin sources across all modules."

    dependsOn(
        provider {
            subprojects.mapNotNull { project ->
                project.tasks.findByName("formatKotlin")
            }
        },
    )
}

tasks.register("verifyKotlinFormat") {
    group = "verification"
    description = "Verifies Kotlin formatting across all modules."

    dependsOn(
        provider {
            subprojects.mapNotNull { project ->
                project.tasks.findByName("lintKotlin")
            }
        },
    )
}