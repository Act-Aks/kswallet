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

include(":core:currencies")
include(":core:database")
include(":core:money")
include(":core:screen")

include(":design-system:catalog")
include(":design-system:core")

include(":feature:accounts:data")
include(":feature:accounts:domain")
include(":feature:accounts:presentation")
