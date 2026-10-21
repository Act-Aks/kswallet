plugins {
    alias(libs.plugins.kswallet.android.library)
    alias(libs.plugins.kswallet.android.di)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room3)
}

android {
    namespace = "com.kswallet.core.database"
}

// Room 3 configures its compiler through the Room Gradle plugin. Schemas are
// generated per variant and MUST be committed: they are what auto-migrations and
// schema-diff tests read.
room3 {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    api(projects.core.model)
    api(libs.androidx.room3.runtime)
    implementation(libs.androidx.sqlite.bundled)
    implementation(libs.kotlinx.datetime)
    ksp(libs.androidx.room3.compiler)
}
