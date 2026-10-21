/**
 * Root build file. Plugins are declared here without being applied so that the
 * version catalog stays the single source of truth for plugin versions, while the
 * actual configuration lives in build-logic convention plugins or module build files.
 */
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room3) apply false
    alias(libs.plugins.koin.compiler) apply false
}
