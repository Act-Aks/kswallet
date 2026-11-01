@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic")

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "KsWallet"

include(":app")

include(
    ":core:currencies", ":core:database", ":core:money", ":core:navigation", ":core:screen"
)

include(
    ":design-system:catalog", ":design-system:core"
)

include(
    ":feature:accounts:data", ":feature:accounts:domain", ":feature:accounts:presentation"
)