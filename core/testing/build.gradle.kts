plugins {
    alias(libs.plugins.kswallet.kotlin.library)
}

dependencies {
    api(projects.core.common)
    api(projects.core.model)
    api(libs.junit4)
    api(libs.kotlinx.coroutines.test)
}
