# KsWallet

A personal-finance app for Android, written in Kotlin: a multi-module, MVI-architected rebuild
of [IvyWallet](https://github.com/philipplackner/IvyWallet-PL_Version) with its own Material 3 design system, its own
package structure and a build that enforces the architecture instead of documenting it.

* **Language / UI:** Kotlin 2.4, Jetpack Compose (Material 3), Android only.
* **Architecture:** MVI, one module per feature, three layers per feature (`data` / `domain` / `presentation`) as
  packages.
* **DI:** Koin 4.2 annotations (`@Single`, `@Factory`, `@KoinViewModel`) plus the Kotlin compiler plugin; the explicit
  `@KoinApplication` graph is validated at **compile time**.
* **Navigation:** Jetpack Navigation 3 (stable 1.2.0) with serializable keys and per-tab back stacks.
* **Persistence:** Room 3 (`androidx.room3`) on a bundled SQLite driver, DataStore for settings.
* **Build:** Gradle 9.8 with convention plugins in `build-logic`, one version catalog.

```bash
./gradlew :app:assembleDebug      # debug APK
./gradlew testDebugUnitTest       # unit tests (no device needed)
./gradlew verifyLayerBoundaries   # architecture check on its own
./gradlew check                   # tests + layer boundaries + Android lint
```

## Status

| Area                                                                                       | State                                                                                                                                                                  |
|--------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Build logic (`build-logic`, version catalog, convention plugins)                           | complete                                                                                                                                                               |
| `:core:*` (common, model, mvi, designsystem, ui, navigation, database, datastore, testing) | complete                                                                                                                                                               |
| Accounts                                                                                   | implemented end-to-end (list, editor, dashboard contract, delete-with-warning)                                                                                         |
| Transactions                                                                               | implemented end-to-end (ledger with filters, editor, transfers, delete)                                                                                                |
| Categories                                                                                 | implemented end-to-end (list with usage counts, editor, delete)                                                                                                        |
| Home                                                                                       | implemented (balance, month cash-flow, recent activity, quick actions)                                                                                                 |
| Settings                                                                                   | implemented (theme mode, wallpaper colours, base currency) — the screen the DataStore exists for                                                                       |
| Budgets                                                                                    | implemented end-to-end (per-category limits over a week/month/year, live spend, progress, editor, delete)                                                              |
| Reports                                                                                    | implemented end-to-end (month picker, income/expense/net summary, daily chart, spending per category with shares, month-over-month comparison, retry on a failed read) |
| Still missing (onboarding, CSV import/export, attachments)                                 | not started — the patterns and modules to copy are in place                                                                                                            |

`./gradlew :app:assembleDebug` produces `app/build/outputs/apk/debug/app-debug.apk` (≈26 MB).
`./gradlew testDebugUnitTest` plus `:core:common:test` and `:core:model:test` runs 181 tests:
128 unit tests on JUnit 6 (Jupiter) plus 53 Compose UI tests that **Robolectric hosts on the JVM**, so no emulator is
involved. The UI tests are written against the JUnit 4 API because Compose ships its test rule only for JUnit 4; the
vintage engine keeps them discoverable, and this box runs them through `tools/run-ui-test.sh` (below). On a machine
where a second JVM does not fit next to the build JVM, `-Pkswallet.skipUiTests=true` keeps the screen tests out of the
Gradle run and `tools/run-ui-test.sh` starts them itself — one JVM, no Gradle.

On a small machine, drive the build through `tools/gbuild.sh` rather than calling Gradle directly: it picks one of three
measured memory profiles, runs a single target per JVM, and keeps the full log. See [
`docs/ARCHITECTURE.md` §12](docs/ARCHITECTURE.md) for the numbers.

## Module map

```
:app                     composition root: annotated Koin graph, navigation graph, bottom bar
:core
  :common                Outcome/DomainError, DateRange, TimeProvider - pure Kotlin
  :model                 Money, Currency, Account, Category, Transaction, ids - pure Kotlin
  :mvi                   MviViewModel<S, I, E>, ObserveAsEvents, UiMessage - pure Kotlin
  :designsystem          KsWalletTheme, colour/type/spacing tokens, KsScreen & state components
  :ui                    formatting (money, dates, colours) and shared leaf components
  :navigation            typed destinations + cross-feature navigation contracts
  :database              Room 3 entities, DAOs, migrations, database factory
  :datastore             user settings (base currency, theme) on Preferences DataStore — also the
                         data layer of :feature:settings, which is why that feature has no data/
  :testing               MainDispatcherExtension, FixedTimeProvider - pure Kotlin test doubles
:feature
  :home                  dashboard built from the accounts + transactions domains
  :transactions          ledger + editor
  :budgets               per-category limits with live spend (reads :feature:categories' domain)
  :reports               month-by-month analysis: aggregates over the ledger, own DAO queries
  :accounts              accounts list + editor
  :categories            categories list + editor
  :settings              theme mode, wallpaper colours and base currency
```

Each `:feature:*` module contains

```
data/           repository implementations, mappers to/from Room entities, DI module
domain/         models, validators, use cases, repository interfaces, DI module
presentation/   MVI contract, ViewModel, Compose screen, navigation entry point, DI module
```

## Where to read next

* [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) — the layers, the rules the build enforces, the MVI contract, the DI
  graph, the Room 3 setup and the navigation route idiom.
* [`build-logic/`](build-logic) — the convention plugins every module applies.

## Requirements

JDK 17+ (the build runs on 21), Android SDK with platform 37 and build-tools 37. Point
`local.properties` at your SDK (`sdk.dir=...`) or export `ANDROID_HOME`.

In an ephemeral container (CI, a coding sandbox) the toolchain is not part of the workspace snapshot, so bring it back
with:

```bash
./tools/bootstrap-toolchain.sh          # downloads JDK 21 + the Android SDK if missing
source ./tools/bootstrap-toolchain.sh   # ...and exports JAVA_HOME / ANDROID_HOME / GRADLE_USER_HOME
```

The script writes `local.properties` for you, makes `gradlew` executable again (file modes do not survive a snapshot
restore) and is safe to re-run.

Then build through the helper, which exists because a ~2 GB container cannot run every Gradle task in one JVM:

```bash
KSWALLET_FLAGS="-Pkswallet.skipUiTests=true" \
  bash tools/gbuild.sh testDebugUnitTest verifyLayerBoundaries \
  :core:common:test :core:model:test                             # 128 unit tests + §4 import rules
bash tools/run-ui-test.sh                                        # 53 Compose UI tests, one JVM
KSWALLET_PROFILE=packaging bash tools/gbuild.sh :app:assembleDebug
KSWALLET_PROFILE=packaging KSWALLET_FLAGS=--configuration-cache \
  bash tools/gbuild.sh :app:lintDebug
```
