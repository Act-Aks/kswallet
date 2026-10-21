plugins {
    alias(libs.plugins.kswallet.kotlin.library)
}

dependencies {
    // Models are pure Kotlin: no Android, no Room, no DI.
    api(projects.core.common)
}
