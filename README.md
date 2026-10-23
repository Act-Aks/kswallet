<div align="center">

# KsWallet

### Your finances. A thoughtfully engineered Android experience.

A Kotlin-first personal-finance app built with Jetpack Compose, Material 3, and a build-enforced modular architecture.

![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-2.4-7F52FF?logo=kotlin&logoColor=white)
![UI](https://img.shields.io/badge/UI-Jetpack_Compose-4285F4?logo=jetpackcompose&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-MVI-6750A4)
![Gradle](https://img.shields.io/badge/Gradle-9.8-02303A?logo=gradle&logoColor=white)

[Features](#features) · [Getting started](#getting-started) · [Architecture](#architecture) · [Testing](#testing) · [Roadmap](#roadmap)

</div>

---

## Overview

KsWallet is an Android personal-finance app for managing accounts, tracking transactions, setting budgets, and reviewing spending.

It is a multi-module, MVI-architected rebuild of [IvyWallet — PL Version](https://github.com/philipplackner/IvyWallet-PL_Version), with its own Material 3 design system, package structure, and architecture checks enforced by the build—not just documented in a guide.

## Features

| Feature | What is implemented |
| --- | --- |
| Home | Balance overview, monthly cash flow, recent activity, and quick actions. |
| Accounts | Account list and editor, dashboard integration, and deletion with a warning. |
| Transactions | Filterable ledger, transaction editor, transfers, and deletion. |
| Categories | Category list with usage counts, editor, and deletion. |
| Budgets | Weekly, monthly, and yearly per-category limits, live spending, progress, editor, and deletion. |
| Reports | Month picker, income/expense/net summaries, daily chart, category spending shares, month-over-month comparison, and retry on failed reads. |
| Settings | Theme mode, wallpaper colours, and base currency. |

Onboarding, CSV import/export, and attachments are not implemented yet. See the [roadmap](#roadmap).

## Tech stack

| Area | Technology |
| --- | --- |
| Language | Kotlin 2.4 |
| UI | Jetpack Compose and Material 3 |
| Architecture | MVI; one module per feature with package-level layers |
| Dependency injection | Koin 4.2 annotations and Kotlin compiler plugin |
| Navigation | Jetpack Navigation 3, stable 1.2.0; serializable keys and per-tab back stacks |
| Database | Room 3 (`androidx.room3`) with a bundled SQLite driver |
| Preferences | Preferences DataStore |
| Build | Gradle 9.8, convention plugins in `build-logic`, and one version catalog |
| Testing | JUnit 6/Jupiter, JUnit 4/Vintage, and Robolectric-hosted Compose UI tests |

The explicit `@KoinApplication` dependency graph is validated at compile time. Dependencies use `@Single`, `@Factory`, and `@KoinViewModel` annotations.

## Getting started

### Requirements

- JDK 17 or newer; the project build runs on JDK 21.
- Android SDK platform 37.
- Android SDK build-tools 37.

### Clone the repository

```bash
git clone https://github.com/Act-Aks/kswallet.git
cd kswallet
```

### Configure the Android SDK

Set the SDK location in your local `local.properties` file:

```properties
sdk.dir=/absolute/path/to/Android/Sdk
```

Alternatively, export `ANDROID_HOME` to your Android SDK directory.

### Build a debug APK

```bash
./gradlew :app:assembleDebug
```

Output:

```text
app/build/outputs/apk/debug/app-debug.apk
```

The documented debug APK size is approximately 26 MB; it can vary between builds.

### Common commands

```bash
# Build the debug APK
./gradlew :app:assembleDebug

# Run debug unit-test tasks; no device required
./gradlew testDebugUnitTest

# Run architecture checks independently
./gradlew verifyLayerBoundaries

# Run checks: tests, layer boundaries, and Android lint
./gradlew check
```

For memory-constrained environments, use the helper workflow below instead of running all tasks directly through Gradle.

## Architecture

KsWallet separates features into Gradle modules and separates each feature's responsibilities into packages. Shared infrastructure lives in `:core:*`, while `:app` is the composition root.

### Module map

```text
:app                       Composition root: Koin graph, navigation graph, bottom bar
│
├── :core
│   ├── :common            Outcome/DomainError, DateRange, TimeProvider
│   ├── :model             Money, Currency, Account, Category, Transaction, IDs
│   ├── :mvi               MviViewModel<S, I, E>, ObserveAsEvents, UiMessage
│   ├── :designsystem      KsWalletTheme, colour/type/spacing tokens, screen/state components
│   ├── :ui                Money/date/colour formatting and shared leaf components
│   ├── :navigation        Typed destinations and cross-feature navigation contracts
│   ├── :database          Room entities, DAOs, migrations, database factory
│   ├── :datastore         Base currency and theme preferences
│   └── :testing           MainDispatcherExtension, FixedTimeProvider
│
└── :feature
    ├── :home              Dashboard using accounts and transactions domains
    ├── :transactions      Ledger and editor
    ├── :budgets           Category limits and live spending
    ├── :reports           Ledger aggregates and report-specific DAO queries
    ├── :accounts          Account list and editor
    ├── :categories        Category list and editor
    └── :settings          Theme, wallpaper colours, and base currency
```

`:core:common`, `:core:model`, `:core:mvi`, and `:core:testing` are pure Kotlin modules.

### Feature structure

Feature layers are packages within a single feature module, not separate Gradle modules:

```text
feature/<feature>/
├── data/                  Repository implementations, Room mappers, DI module
├── domain/                Models, validators, use cases, repository interfaces, DI module
└── presentation/          MVI contract, ViewModel, Compose screen, navigation entry, DI module
```

The settings feature is the exception: `:core:datastore` supplies its data layer, so `:feature:settings` has no `data/` package.

### Architectural building blocks

- MVI: screen state, user intents, and effects use the shared `MviViewModel<S, I, E>` foundation.
- Dependency injection: an explicitly annotated Koin application graph is checked at compile time.
- Navigation: serializable Navigation 3 keys support typed destinations and separate back stacks per tab.
- Persistence: Room 3 provides database storage; Preferences DataStore stores user settings.
- Build enforcement: `verifyLayerBoundaries` checks architectural boundaries independently and as part of the checks workflow.

Read [the architecture guide](docs/ARCHITECTURE.md) for layer rules, MVI contracts, DI configuration, Room setup, and navigation conventions. Shared module configuration lives in [build-logic](build-logic/).

## Testing

The documented suite contains 181 tests:

| Suite | Count | Runtime |
| --- | --- | --- |
| Unit tests | 128 | JUnit 6 / Jupiter |
| Compose UI tests | 53 | Robolectric on the JVM, using JUnit 4 APIs and the Vintage engine |

No emulator is required for these tests. Compose UI tests use JUnit 4 because Compose's test rule is supplied through that API; the Vintage engine keeps them discoverable.

The documented test tasks include:

```bash
./gradlew testDebugUnitTest :core:common:test :core:model:test
```

On machines that cannot fit a separate test JVM alongside Gradle, skip UI tests in the Gradle run and execute them through the standalone helper:

```bash
KSWALLET_FLAGS="-Pkswallet.skipUiTests=true"   bash tools/gbuild.sh testDebugUnitTest verifyLayerBoundaries   :core:common:test :core:model:test

bash tools/run-ui-test.sh
```

The first command runs the 128 unit tests and layer-boundary checks. The second runs the 53 Compose UI tests in one JVM without Gradle.

## Low-memory and ephemeral environments

### Restore the toolchain

In CI containers or coding sandboxes, the JDK and Android SDK may not survive a workspace snapshot. Restore them with:

```bash
# Download JDK 21 and the Android SDK if missing
./tools/bootstrap-toolchain.sh

# Source the script to also export the toolchain environment variables
source ./tools/bootstrap-toolchain.sh
```

The script sets up `local.properties`, restores the executable permission on `gradlew`, and can be rerun safely. Sourcing it exports `JAVA_HOME`, `ANDROID_HOME`, and `GRADLE_USER_HOME` into the current shell.

### Use the memory-aware build helper

`tools/gbuild.sh` selects from three measured memory profiles, executes a single target per JVM, and retains the full log. It is intended for small environments, including containers with approximately 2 GB of memory.

```bash
# Unit tests and architecture checks
KSWALLET_FLAGS="-Pkswallet.skipUiTests=true"   bash tools/gbuild.sh testDebugUnitTest verifyLayerBoundaries   :core:common:test :core:model:test

# Compose UI tests in a standalone JVM
bash tools/run-ui-test.sh

# Debug APK
KSWALLET_PROFILE=packaging bash tools/gbuild.sh :app:assembleDebug

# Android lint
KSWALLET_PROFILE=packaging KSWALLET_FLAGS=--configuration-cache   bash tools/gbuild.sh :app:lintDebug
```

See [Architecture §12](docs/ARCHITECTURE.md) for measured memory usage and profile details.

## Roadmap

- [x] Build logic, version catalog, and convention plugins
- [x] Shared core modules
- [x] Accounts
- [x] Transactions and transfers
- [x] Categories
- [x] Home dashboard
- [x] Settings
- [x] Budgets
- [x] Reports
- [ ] Onboarding
- [ ] CSV import/export
- [ ] Attachments

## Documentation

| Resource | Contents |
| --- | --- |
| [Architecture guide](docs/ARCHITECTURE.md) | Layers, build-enforced rules, MVI, DI, database, navigation, and memory profiles |
| [Build logic](build-logic/) | Convention plugins shared across modules |

## Acknowledgements

KsWallet rebuilds [IvyWallet — PL Version](https://github.com/philipplackner/IvyWallet-PL_Version) with its own modular structure, Material 3 design system, and build-enforced architecture.
