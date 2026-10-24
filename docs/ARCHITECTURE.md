# KsWallet — architecture

This document is the contract the code is written against. Where a rule can be checked by a machine, it is:
see [Dependency rules enforced by the build](#dependency-rules-enforced-by-the-build).

---

## 1. Module graph

```
                              :app
                               │  composition root
   ┌────────┬───────────┬──────────┬──────────┬───────────┬────────────┬───────────┐
   │        │           │          │          │           │            │           │
:feature:home :feature:transactions :feature:budgets :feature:reports :feature:accounts :feature:categories :feature:settings
   │        │           │          │          │           │            │           │
   └────────┴───────────┴──────────┴──────────┴───────────┴────────────┴───────────┘
                               │
   ┌──────────┬──────────┬─────┴─────┬───────────┬──────────────┐
   │          │          │           │           │              │
:core:mvi  :core:ui  :core:designsystem  :core:navigation  :core:datastore
   │          │                                              :core:database
   └──────────┴──────────────┬───────────────────────────────────┘
                             │
                 :core:common   :core:model   :core:testing
                    (pure Kotlin — no Android, no AndroidX, no Koin)
```

* `:core:*` never depends on a feature.
* A feature depends on `:core:*` and — when it genuinely needs another feature's data — on that feature's **domain**
  layer only (`:feature:home` reads the accounts and transactions domains; it never sees their DAOs or their
  ViewModels).
* `:app` is the only module that knows all features. It owns the Koin module list, the navigation graph and the bottom
  bar.

Why one module per feature instead of one per layer: layers are packages here, so a feature is a single compilation unit
that can be built, tested and reviewed on its own, while the layer rules stay enforceable (see §4).

One feature deviates from the three-layer template on purpose: `:feature:settings` has **no
`data/`** — its storage is `:core:datastore`, a module of its own (see §8).

`:feature:budgets` is the one feature that depends on another feature: the budget editor has to list expense categories,
and it reads them through `:feature:categories`' **domain** layer (`ObserveCategoriesUseCase`). That is the only kind of
cross-feature dependency the boundary check allows (§4) — the categories' DAO, mappers and ViewModel stay invisible to
it, and it is a one-way street: categories knows nothing about budgets.

`:feature:reports` deliberately depends on **no** other feature, not even to read the ledger. It owns the queries it
needs (`TransactionDao.observeCategorySpendWithDetails`,
`observeCashFlowPoints`) rather than borrowing the transactions feature's use cases, because
"what did I spend per category this month" is a different question from "show me my ledger" - and because the answer has
to stay aggregatable in SQL (§6).

## 2. The three layers inside a feature

```
feature/<name>/src/main/kotlin/com/kswallet/feature/<name>/
├── data/
│   ├── repository/OfflineFirst<X>Repository.kt   implements the domain interface
│   ├── mapper/<X>Mapper.kt                       entity <-> domain model
│   └── di/<X>DataModule.kt                       Koin bindings for this layer
├── domain/
│   ├── model/                                    value objects the UI and the DB both need
│   ├── repository/<X>Repository.kt               interface + Outcome-returning contract
│   ├── usecase/<X>UseCases.kt                    one class, one `invoke`
│   ├── validation/<X>FormValidator.kt            pure `object` rules for the editors
│   └── di/<X>DomainModule.kt
└── presentation/
    ├── <screen>/<X>Contract.kt                   UiState / Intent / Effect
    ├── <screen>/<X>ViewModel.kt                  MVI, no Android types
    ├── <screen>/<X>Screen.kt                     <X>Route (stateful) + <X>Screen (stateless)
    ├── error/<X>ErrorMessages.kt                 DomainError -> UiMessage
    ├── navigation/<X>Navigation.kt               NavKey destinations + entry-provider extension
    └── di/<X>PresentationModule.kt
```

What each layer may do:

| Layer          | May use                                                           | Must not use                                             |
|----------------|-------------------------------------------------------------------|----------------------------------------------------------|
| `domain`       | Kotlin, `:core:common`, `:core:model`, another feature's `domain` | Android, AndroidX, Compose, Koin, `data`, `presentation` |
| `data`         | `domain`, Room (`:core:database`), coroutines, Koin               | `presentation`, another feature's model/DAO              |
| `presentation` | `domain`, Compose, `:core:mvi/ui/designsystem/navigation`, Koin   | `data` (including DAOs), another feature's UI            |

The one deliberate exception: `<layer>/di/` is *wiring*, so the framework rules do not apply to it.
`domain/di/AccountsDomainModule.kt` may import Koin — no other file in `domain/` may.

## 3. MVI contract

```kotlin
// 1. the contract — the only file the screen and the ViewModel share
data class WalletUiState(...) : MviState
sealed interface WalletIntent : MviIntent { ... }
sealed interface WalletEffect : MviEffect { ... }

// 2. the ViewModel — pure logic, no Context, no Compose
class WalletViewModel(...) : MviViewModel<WalletUiState, WalletIntent, WalletEffect>(WalletUiState()) {
    override fun onIntent(intent: WalletIntent) {
        ... setState { ... } / emitEffect(...)
    }
}

// 3. the screen — stateful `Route` + stateless `Screen`
@Composable
fun WalletRoute(viewModel: WalletViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.effects) { /* navigate or show a snackbar */ }
    WalletScreen(state = state, onIntent = viewModel::onIntent)
}

@Composable
fun WalletScreen(state: WalletUiState, onIntent: (WalletIntent) -> Unit, modifier: Modifier = Modifier)
```

Rules that hold everywhere in this codebase:

* **One immutable state object per screen.** A screen is a pure function of its state, so a UI test (or a preview) only
  needs a state value.
* **Effects are one-shot.** `MviViewModel` uses a `Channel`, so a navigation effect cannot be replayed after a
  configuration change; `ObserveAsEvents` collects it with the lifecycle.
* **No `Context` in a ViewModel.** User-facing text travels as `UiMessage.Resource(id)`, which also keeps every
  ViewModel unit-testable on the JVM (all ViewModel tests run without a device or Robolectric).
* **Filtering and sorting live in the domain/usecase, not in the composable.** The transactions filter is a value object
  (`TransactionFilter`) that the ViewModel turns into a query and the use case resolves into a date range.

## 4. Dependency rules enforced by the build

`verifyLayerBoundaries` (registered by every convention plugin, wired into `check`) parses the `import` lines of every
source file and fails the build with a file-and-line list:

| # | Rule                                                                                                   |
|---|--------------------------------------------------------------------------------------------------------|
| 1 | `domain` must not import `android.*` / `androidx.*`, Koin, `data`, or `presentation`                   |
| 2 | `data` must not import `presentation`                                                                  |
| 3 | `presentation` must not import `data` (or `:core:database` / `:core:datastore.data` directly)          |
| 4 | a feature may only import another feature's `domain.*` or `presentation.navigation.*`                  |
| 5 | `:core:common`, `:core:model`, `:core:testing` must stay pure Kotlin (no Android/AndroidX/Koin at all) |
| — | `<layer>/di/**` is exempt from the *framework* rules in row 1 only; the layer rules still apply to it  |

Because rules 4 and 5 are checked per import, a leak fails in the module that introduced it — with the offending import
in the error message — instead of surfacing as a runtime surprise.

## 5. Dependency injection (Koin 4)

* DI definitions use **Koin annotations** throughout: repositories and the database factory use
  `@Single`, screen ViewModels use `@KoinViewModel`, and use-case factories are `@Factory`
  provider functions in their layer's DI module. Keeping those provider functions in `domain/di/`
  lets domain logic remain framework-free. Each layer's `@Module` is colocated under its `di/`
  package; annotated implementations are gathered with `@ComponentScan` where appropriate.
* `:app` is the explicit composition root. `@KoinApplication` on
  `KsWalletKoinApplication` lists every core and feature module; adding a feature therefore requires a visible entry in
  that annotation rather than an implicit classpath-wide scan.
  `KsApplication` starts it with the compiler-plugin typed `startKoin<KsWalletKoinApplication>()`.
* The **Koin compiler plugin** (`io.insert-koin.compiler.plugin`) validates the full graph when `:app` compiles: a
  missing binding is a compile error (`[Koin][KOIN-D001] Missing
  dependency: …`). This is why the app has no `checkModules` test — the build is stricter than a runtime-only test. The
  app graph test also guards the explicit module list.
* Interfaces are bound on implementations, e.g. `@Single(binds = [AccountRepository::class])`; constructor injection
  wires dependencies without handwritten `get()` calls. Room and the app clock are provided by annotated module
  functions because they are built by factories.
* Parameterised editors use `@InjectedParam` on their ID constructor parameter. The call site remains
  `koinViewModel<AccountEditorViewModel> { parametersOf(accountIdValue) }`, so the current editor ID is a call-time
  value and never a global binding.
* `TimeProvider` is a singleton: "now" must be the same clock for the ledger's day grouping and the monthly reports.
  Tests swap it for `FixedTimeProvider`.

## 6. Persistence

**Room 3** (`androidx.room3`, KSP code generation, Kotlin-only). Room 3 is a driver-API database — SupportSQLite is
gone — so `WalletDatabaseFactory` installs the
`BundledSQLiteDriver` and runs queries on `Dispatchers.IO`. One database, four entities:

```
accounts(id, name, type, currency_code, initial_balance_minor, icon_key, color_argb,
         included_in_total, is_archived, created_at)
categories(id, name, type, icon_key, color_argb, created_at)
transactions(id, type, amount_minor, currency_code, account_id, transfer_account_id,
             category_id, note, date_time, created_at)
budgets(id, category_id -> categories(id) ON DELETE CASCADE, limit_minor, currency_code,
        period, created_at)          unique (category_id, period)
```

* **Money never floats.** `Money` is minor units (`Long`) + `Currency`, converted in the mapper; sums happen in SQL
  where the list screen needs a balance.
* **Balances are queries, not columns** (`AccountDao` computes the balance from the initial balance plus the
  transactions), so an edit can never leave a stale total behind.
* **`Flow` or `suspend` only** — Room 3 has no blocking DAO functions, which makes "off the main thread" a compile-time
  property.
* **Reports are queries too.** `TransactionDao.observeCategorySpendWithDetails` sums a category's expenses with a
  `LEFT JOIN` (so uncategorised spending is a row, not a gap), and
  `observeCashFlowPoints` returns income/expense per *instant*: which calendar day an instant belongs to depends on the
  user's time zone, which lives in `TimeProvider`, so the SQL stays time-zone-free and the repository does the
  rolling-up. Shares are computed from the rows themselves, so they always add up to 1.
* **Budget spend is never stored, only derived.** `BudgetDao.observeCategorySpend(from, to)`
  sums the expense transactions of each category *in the currency of the account they were paid from*, and
  `OfflineFirstBudgetRepository` looks those sums up for the window each budget's period covers. A budget can therefore
  never disagree with the ledger, and a category that is deleted takes its budget with it (the foreign key cascades).
* `exportSchema = true`: `core/database/schemas/…/1.json` and `2.json` are committed. Adding `budgets` was a pure
  addition, so it ships as `@AutoMigration(from = 1, to = 2)` and Room verifies that migration against the two schema
  files at compile time (`WalletMigrations.ALL` stays empty until a change is *not* an addition).
* Repositories return `Outcome<T>` (`Success` / `Failure(DomainError)`) and never throw across a layer boundary.

**DataStore** (`:core:datastore`) holds `UserSettings` (theme mode, dynamic colour, base currency, onboarding flag)
behind `SettingsRepository`; the flow is collected at the top of the app, so a change re-themes the UI without a
restart.

## 7. Navigation

KsWallet uses **Jetpack Navigation 3** (`androidx.navigation3`, stable 1.2.0). Every route is a
`@Serializable` key implementing `NavKey` through the shared `KsDestination` contract; there are no string routes,
`NavController`, or `NavHost` graph. `:core:navigation` owns cross-feature contracts (`TransactionEditorRoot`,
`AccountDetailRoot`) and stack operations.

Each feature declares its route keys and exposes an
`EntryProviderScope<NavKey>.<feature>Entries()` extension. The app composes those entry builders inside one
`entryProvider` and renders them with `NavDisplay`. This keeps feature UI private while allowing the app to wire
hand-offs such as home → editor and accounts → account detail.

The app maintains an independent `rememberNavBackStack` for each bottom tab and stores the selected tab name with
`rememberSaveable`. Switching tabs therefore retains each tab's back stack; back first pops the active stack and, at a
tab root, returns to Home. The bottom bar is the
`KsTopLevelDestination` enum, whose entries bind each root key to its label and icon. Editors and the account detail
hide the bar, matching the previous behavior.

`NavDisplay` uses `rememberSaveableStateHolderNavEntryDecorator()` for Compose state and
`rememberViewModelStoreNavEntryDecorator()` from Lifecycle's Navigation 3 integration so Koin ViewModels are scoped to
individual entries and are cleared when entries leave their stack. The app is Android-only, so `rememberNavBackStack`
uses Navigation 3's Android serialization path for the open set of serializable feature keys; a multiplatform port
should provide a registered
`SavedStateConfiguration` instead.

## 8. Design system

`:core:designsystem` is the only module that knows about Material 3 theming:

* `KsWalletTheme` (light/dark/dynamic colour) provides `MaterialTheme` plus a custom
  `LocalKsSpacing`; colours, type scale and shapes are tokens (`KsColor`, `KsType`, `KsShape`,
  `KsSpacing`) rather than literals in screens.
* `KsIcons` maps stable string keys to `ImageVector`s, because categories and accounts store an icon *key* in the
  database (never a drawable id) and the UI resolves it.
* Shared screen furniture: `KsScreen` (scaffold + title + optional back arrow, snackbar host, actions and FAB),
  `KsLoadingState`, `KsEmptyState`, `KsErrorState`, `KsConfirmDialog`,
  `KsSectionHeader`.
* `:core:ui` adds formatting that several features need (`MoneyFormatter`, `DateFormatter`, colour extensions) and leaf
  components (`KsAmountText`, `KsMessageHost`).

## 9. Build logic

`build-logic` is an included build exposing six convention plugins; every module build file is a handful of lines.

| Plugin id                          | Applies / adds                                                                                               |
|------------------------------------|--------------------------------------------------------------------------------------------------------------|
| `kswallet.kotlin.library`          | Kotlin JVM 17, unit-test deps, framework-free layer check, `api`/`implementation` helpers                    |
| `kswallet.android.library`         | `com.android.library`, SDK levels 26/36, Java/Kotlin 17, lint config, unit + instrumented tests, layer check |
| `kswallet.android.library.compose` | the above + Compose build feature, Compose compiler plugin, Compose BOM deps                                 |
| `kswallet.android.di`              | Koin BOM (core, android, compose, compose-viewmodel, annotations) + the Koin compiler plugin                 |
| `kswallet.android.feature`         | Compose library + DI + the `:core:*` modules every feature needs (and `:core:testing` for tests)             |
| `kswallet.android.application`     | `com.android.application`, app id, versioning, Kotlin/Compose/DI, layer check                                |

* **One version catalog** (`gradle/libs.versions.toml`) is the single source of truth for versions; the convention
  plugins read it through the shared `libs` accessor, and module build files use typed `libs.` aliases only.
* `compileSdk` is **37** (Android 17), required by Navigation 3 1.2.0. The app's
  `targetSdk` remains 36 so Android 17 behavior changes are adopted separately and deliberately.
* **AGP 9** facts the conventions encode: no `kotlin-android` plugin and no `kotlinOptions`
  (configuration lives on the `kotlin` extension), `CommonExtension` is non-generic, and lint is configured as a
  property.
* Lint: `abortOnError`, `checkDependencies` (a module is linted with its dependencies), version-nag checks disabled. The
  build currently reports **0 errors and 2 warnings**:
  `OldTargetApi` (the SDK 37 platform is newer than the pinned `targetSdk 36` — bumping it means moving the whole
  catalog at once, see §Build logic) and `ObsoleteSdkInt` on
  `res/mipmap-anydpi-v26`, which has to keep its `-v26` qualifier because AAPT2 only links
  `<adaptive-icon>` out of a version-qualified folder.
* Layer boundaries are enforced by `verifyLayerBoundaries` (§4), not by review.

## 10. Testing

* The unit-test suite runs on **JUnit 6 (Jupiter)**: `junit-bom` 6.1.3, `useJUnitPlatform()`
  in every module's test task. The migration from JUnit 4 was mostly imports, plus the two places the frameworks
  genuinely differ: the `MainDispatcherRule` JUnit 4 rule became `MainDispatcherExtension` (a `BeforeEachCallback`/
  `AfterEachCallback` registered with `@RegisterExtension`, which is Jupiter's replacement for `@get:Rule`), and
  three-argument assertions moved their message from first to last (`assertEquals(msg, expected, actual)` →
  `assertEquals(expected, actual, msg)`). The screen tests are the JUnit 4 holdouts, for two upstream reasons: Compose
  ships its test rule only as `androidx.compose.ui:ui-test-junit4`, and Robolectric has no official Jupiter support (the
  community extension is experimental). The **vintage engine** keeps those JUnit 4 classes discoverable on the JUnit
  Platform, so a machine with the memory for it can still run them through Gradle; this box runs them through
  `tools/run-ui-test.sh` instead (below).
* `:core:testing` provides `MainDispatcherExtension` (coroutines on a test dispatcher) and
  `FixedTimeProvider` ("today" is `2026-10-12`, never "whenever the CI ran").
* Unit tests (128 today, plus the 53 UI tests described at the end of this section) cover the parts where bugs are
  quiet: `Money` arithmetic and parsing, `Outcome`/`DomainError` mapping, date ranges, the four form validators, the
  ledger's day grouping and subtotals, and every feature ViewModel (state after load, empty vs loading, filter changes,
  delete confirmation flow, effects).
* The budgets repository is tested through a fake DAO rather than through Android: the test asserts which window each
  period asks for and that spending in another currency is ignored, which is exactly the part that is SQL in production
  and easy to get subtly wrong. The reports repository uses MockK with a strict DAO instead, and verifies the
  *arguments* the queries receive (the millisecond window of a month, computed independently in the test with
  `java.time`) plus the day rollup `2026-10-05T20:00Z -> day 6` in a `+05:30` time zone.
* One test earned its keep immediately: `retrying subscribes again and clears the error` in
  `ReportsViewModelTest` failed on first run because a `catch` at the end of the flow chain completes the stream, after
  which the retry counter could never re-subscribe. The fix was to move the `catch` inside `flatMapLatest`, where a
  failed read ends only its own attempt.
* `:app` tests assert the composition root (every module listed exactly once) and the bottom bar (order, unique
  routes/icons).
* **Compose UI tests run on the JVM, not on an emulator.** `ReportsScreenTest` and
  `BudgetsScreenTest` (9 tests each), `AccountsScreenTest` (9), `TransactionsScreenTest`
  (11), `CategoriesScreenTest` (8) and `SettingsScreenTest` (7) render a real screen with
  `createComposeRule` and drive it through `onIntent`; Robolectric boots the Android runtime for them inside the test
  worker, which is the difference between "a UI test exists" and "a UI test runs in CI". A few environment details are
  worth having in hand before writing the next one:
    * `@Config(sdk = [35])` is required. On SDK 36 Robolectric 4.17 fails to set up its shared memory
      (`ApplicationSharedMemory.create` → `FileDescriptorInterceptor.setInt`,
      "Failed to interact with raw FileDescriptor internals") and *every* test in the class fails before its first
      assertion. `--add-opens=java.base/java.{io,lang,util,nio}` (which the convention plugin sets) does not help; SDK
      35 does.
    * A string that sits below the fold is reported as "not displayed". Scroll to it first —
      `onNode(hasScrollAction()).performScrollToNode(hasText(text))` for a lazy list, or
      `onNodeWithText(text).performScrollTo()` for a plain scrollable column — which is what a user does anyway.
    * The label of a Material 3 **extended** FAB is drawn but wrapped in `clearAndSetSemantics`, so it never reaches the
      merged semantics tree and `onNodeWithText` finds nothing at all. Assert it with `useUnmergedTree = true`; clicking
      it injects the touch at the label's own position and lands on the button underneath.
    * There is no `assertIsSelected()` in this compose-test version: a chip, tab or row carries its selection in
      semantics, so assert it with
      `.assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))`.
    * The same word can be both a summary label and a filter chip ("Income" is both on the transactions screen), so
      `onNodeWithText` finds two nodes and fails. Assert the unique thing — the formatted amount — and let the chip test
      click a chip whose text appears nowhere else.
    * A `Switch` and the currency rows' `RadioButton`s are both toggleable; what tells them apart is their role, so find
      the switch with
      `SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Switch)`.
    * The test display is 320×470 dp, and long text at the bottom of a scrollable column comes back from the semantics
      tree clipped to a sliver, so `assertIsDisplayed` fails even after scrolling to it. For static text that is
      informational rather than a control, `assertExists()` is the honest check; the controls above it are checked
      displayed.
* What a screen test asserts is the part no ViewModel test can see: that each budget row's bar carries *that row's*
  fraction (`hasProgressBarRangeInfo(ProgressBarRangeInfo(0.4f, 0f..1f))`), that being over budget is said in words and
  not only in red, that deleting goes through the confirmation dialog (and a dismissal through the other branch of the
  contract), that the ledger groups by day with a signed subtotal per group, that an account left out of the total says
  so next to its type, that the theme/base-currency/dynamic-colour choices send the intent that carries the whole new
  value, and that a report's category list adds up to the shares of the month on screen.

## 11. Deliberate non-goals / next steps

* **More screen tests.** All six list screens have one (accounts, transactions, categories, settings, budgets, reports —
  53 tests in total). The editors do not:
  account, transaction and category forms are the screens with the most state (text fields, pickers, a transfer's two
  account pickers, validation errors). Every screen is stateless (`XScreen(state, onIntent)` next to a thin `XRoute`),
  so each is a few lines: build the state, assert what is rendered, tap, check the intent.
* **Recurring transactions and budget alerts** would sit on top of the existing domain:
  `TimeProvider` already gives every period a single, testable definition of "this month"
  (`weekRangeOf` / `monthRangeOf` / `yearRangeOf`), which is what a rollover or a notification would compute against.
* Attachments, CSV import/export and onboarding are not written yet: `:feature:categories`,
  `:feature:accounts` and `:feature:transactions` are the templates to copy (contract → ViewModel → screen →
  navigation → three DI modules → tests).
* No offline/remote sync: the repositories are named `OfflineFirst*` because the seam for a remote data source is the
  repository, not the ViewModel.
* Kotlin Multiplatform was considered and rejected for this project: Android-only keeps the build, the design system and
  the tests simple.

## 12. Building it on a small machine

The whole build was developed and verified inside a 2-core, ~2 GB container, so the memory recipe in `gradle.properties`
is deliberate rather than cargo-culted:

* `kotlin.compiler.execution.strategy=in-process` keeps the Kotlin compiler in the Gradle daemon (one JVM instead of
  two), which is why `-XX:MaxMetaspaceSize` matters as much as
  `-Xmx`: a *cold* build of every module (Compose + material-icons-extended + Room/KSP + the Koin plugin) loads far more
  classes than an incremental one, and too small a metaspace shows up as `OutOfMemoryError: Metaspace` inside a compile.
* `-XX:ReservedCodeCacheSize=256m` is not decoration: Room's generated code runs into the code cache during KSP if it is
  smaller.
* `org.gradle.workers.max=1` keeps a second worker process from doubling the peak.
* Heavy phases are better run one at a time on such a box: assembling the APK (D8 merging the dex archives needs a large
  heap) and lint (which with `checkDependencies` analyses every module's sources) each fit comfortably alone, and less
  so together. On a workstation
  `./gradlew check` in one go is fine.
* **Only ~900 MB of anonymous memory is left for a JVM.** Measured on this box: the platform's own services hold ~600 MB
  (they grew since the first measurement), the container has 1.9 GB and no swap, and the kernel kills any JVM whose
  resident set passes ~900 MB — `dmesg` records
  `Out of memory: Killed process … anon-rss:892652kB`, and Gradle reports only "Gradle build daemon disappeared
  unexpectedly". Two consequences that cost real time to learn:
    * **A full recompile does not fit in one JVM.** With
      `kotlin.compiler.execution.strategy=in-process`, one Gradle JVM that compiles every module reaches 915 MB and
      dies, no matter how the heap is sized (`-Xmx480m` included: the resident set is heap + metaspace + code cache +
      native). After a build-logic change — which dirties every module's compile task — build in *groups of modules*,
      one Gradle invocation per group, so each JVM starts from a clean metaspace and stays well under the line.
      Completed tasks persist, so a run that is killed is resumed rather than repeated.
    * **The UI tests need a JVM the build JVM cannot share the box with.** A Gradle-hosted Robolectric run needs three
      JVMs at once (client + build + test worker); that combination wedges the box for many minutes and eventually loses
      the build JVM. So the screen tests are run by `tools/run-ui-test.sh`, which replays the exact classpath and system
      properties of the Gradle `Test` task (dumped by `tools/dump-test-classpath.init.gradle`) into a single JVM with a
      heap you choose. Gradle skips them with `-Pkswallet.skipUiTests=true`.
* **Raising `-Xmx` makes a build *less* likely to finish, not more.**
  giving GC a smaller heap is the fix for the OOM, and `-XX:MaxDirectMemorySize` matters because D8 and lint keep
  buffers outside the heap. `tools/gbuild.sh` encodes the three profiles that actually work here (`cold`, `packaging`,
  `warm`), prints the one it picked, runs a single target per invocation, and keeps the full log (`/tmp/gbuild.log` by
  default).
* **The debug APK no longer assembles here, and dex merging is why.** `:app:mergeExtDexDebug`
  runs D8 inside the build JVM, and its appetite sits exactly on that line: at `-Xmx600m` it fails with
  `D8: java.lang.OutOfMemoryError: Java heap space`, and at `-Xmx700m` the kernel kills the JVM at 918 MB of anonymous
  memory. There is no heap size between the two that both satisfies D8 and stays under the ~900 MB the box has left. The
  APK committed with this project (27 284 373 B, 25 dex files, reports and budgets included) was built while ~1.2 GB was
  still available for one JVM; on a container that does not share its RAM with a notebook service, or on a workstation,
  `KSWALLET_PROFILE=packaging bash tools/gbuild.sh :app:assembleDebug` is the same command that produced it. Nothing in
  `src/main` has changed since.
* `--configuration-cache` pays for itself on this box: skipping the configure phase removes the Kotlin-DSL script
  classes from the metaspace of every later run, which is often the difference between lint finishing and lint being
  killed.
* **A cold Gradle home is a different animal from a cold compile.** With an empty
  `GRADLE_USER_HOME` - which is what a restored container has - the first build resolves and verifies every artifact of
  17 modules inside the same JVM that compiles them. At 800 MB of heap that turns into GC thrash rather than a clean
  failure: the daemon sits at its ceiling (~1.4 GB RSS, ~30% CPU) for minutes per task. The recovery is cheap - let the
  *downloads*
  finish, because that is what the heap was fighting for, then re-run. `tools/gbuild.sh` prints the profile it picked,
  so a slow run can be recognised as a memory problem.
* **Lint is the one task that may need a module instead of the app.** `:app:lintDebug` pulls every module's sources and
  binary resources into a single analysis; on this box that needs
  ~950 MB of heap *and* a warm configuration cache (and it caught nothing new). For a feature branch,
  `:feature:reports:lintDebug` answers the same question about the code that actually changed, in seconds - it reported
  "No issues found." for both modules touched by reports.

A full cold verification of this repository is a handful of separate invocations - and after a build-logic change the
compile tasks are dirty in *every* module, so the compiles go in groups (the suite invocation then finds them up to
date):

```bash
bash tools/bootstrap-toolchain.sh                                  # once per fresh sandbox
bash tools/gbuild.sh :core:common:compileKotlin :core:model:compileKotlin \
  :core:mvi:compileDebugKotlin :core:navigation:compileDebugKotlin \
  :core:ui:compileDebugKotlin :core:datastore:compileDebugKotlin    # pure-Kotlin modules use
                                                                   # `compileKotlin`, the
                                                                   # Android ones `compileDebugKotlin`
bash tools/gbuild.sh :core:database:compileDebugKotlin :core:designsystem:compileDebugKotlin
bash tools/gbuild.sh :feature:accounts:compileDebugKotlin :feature:categories:compileDebugKotlin \
  :feature:settings:compileDebugKotlin
bash tools/gbuild.sh :feature:budgets:compileDebugKotlin :feature:reports:compileDebugKotlin \
  :feature:home:compileDebugKotlin
bash tools/gbuild.sh :feature:transactions:compileDebugKotlin      # the big one, alone
bash tools/gbuild.sh :app:compileDebugKotlin
KSWALLET_FLAGS="-Pkswallet.skipUiTests=true" \
  bash tools/gbuild.sh testDebugUnitTest verifyLayerBoundaries \
  :core:common:test :core:model:test                               # 128 unit tests
bash tools/run-ui-test.sh                                          # 53 Robolectric UI tests
KSWALLET_PROFILE=packaging bash tools/gbuild.sh :app:assembleDebug # APK ≈ 26 MB - needs ~1.2 GB
                                                                   # free for one JVM, which
                                                                   # this box no longer has
bash tools/gbuild.sh :feature:reports:lintDebug                    # per module, seconds
KSWALLET_FLAGS=--configuration-cache KSWALLET_JVMARGS="-Xmx950m \
  -XX:MaxMetaspaceSize=220m -XX:MaxDirectMemorySize=128m -XX:+UseSerialGC" \
  bash tools/gbuild.sh :app:lintDebug                              # 0 errors, 2 warnings
```
