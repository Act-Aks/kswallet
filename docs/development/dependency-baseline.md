# Dependency baseline

Captured from the user's version catalog on 2026-10-28. These are project pins,
not a recommendation to update to whatever a search calls latest.

| Component | Project version |
|---|---|
| AGP | 9.4.1 |
| Kotlin | 2.4.21 |
| KSP | 2.3.12 |
| Compose | 1.12.1 |
| Activity | 1.13.0 |
| Lifecycle | 2.11.0 |
| Coroutines | 1.11.0 |
| Koin | 4.2.2 |
| Room 3 | 3.0.3 |
| SQLite | 2.7.1 |
| JUnit BOM | 6.1.3 |
| JUnit 4 | 4.13.2 |
| AndroidX test runner | 1.7.0 |
| AndroidX ext JUnit | 1.3.0 |
| Espresso | 3.7.0 |

## Android / JVM / application

- compileSdk: 37
- targetSdk: 37
- minSdk: 26
- JVM/Java: 17
- applicationId: com.kswallet
- application version: 0.1.0 / versionCode 1
- Neo catalog applicationId: com.neodesign.catalog
- Neo catalog version: 0.1.0 / versionCode 1

## Convention plugin IDs

- kswallet.android.application
- kswallet.android.compose
- kswallet.android.library
- kswallet.android.room
- kswallet.kotlin.library

AGP 9 Android configuration is already selected by the project. Do not blindly
apply legacy Kotlin Android plugin instructions from unrelated examples.

## Accessors evidenced in the supplied catalog

```kotlin
libs.androidx.lifecycle.viewmodel
libs.androidx.lifecycle.runtime.ktx
libs.androidx.lifecycle.runtime.compose
libs.kotlinx.coroutines.core
libs.kotlinx.coroutines.android
libs.kotlinx.coroutines.test
libs.koin.core
libs.koin.android
libs.koin.androidx.compose
libs.koin.test
libs.androidx.room3.runtime
libs.androidx.room3.compiler
libs.androidx.sqlite.bundled
```

libs.androidx.lifecycle.viewmodel.ktx does not exist in this catalog.
The dependency reference should follow the catalog, not the other way around.

## Source of truth

gradle/libs.versions.toml remains authoritative. Update this snapshot after planned
upgrades, but do not replace the catalog with manually maintained dependency lists.
Compatibility is established by the project build/tests, not by this table alone.
