# Verification checklist

No commands in this document were executed while preparing the documentation.
Record actual results, device/API, and commit before marking the snapshot verified.

## Build logic

```bash
./gradlew -p build-logic :convention:build
```

## JVM suites

```bash
./gradlew :core:money:test :core:currencies:test :feature:accounts:domain:test
```

## Android builds

```bash
./gradlew :core:database:assembleDebug :feature:accounts:data:assembleDebug :feature:accounts:presentation:assembleDebug :app:assembleDebug
```

## Instrumentation suites (connected emulator/device required)

```bash
./gradlew :core:database:connectedDebugAndroidTest :feature:accounts:data:connectedDebugAndroidTest :app:connectedDebugAndroidTest
```

## Application local tests

```bash
./gradlew :app:testDebugUnitTest
```

These commands reflect the module/task names used in the conversation. Confirm
against the repository; do not change working build conventions to force a stale
checklist to match.

## Schema / Git inspection

```bash
git status --short
git diff --check
git diff -- core/database/schemas
```

- Verify schema version 1 exists under the database module's schemas directory.
- DAO-only changes should not introduce table-schema changes.
- Commit exported schemas.
- Confirm exactly one AccountDao and one global Koin startup.
- Confirm project-owned database code uses Room 3.
- Confirm isolated test DBs are closed and deleted.
- No production sample-account insertion on startup.
- No invented balances or destructive fallback.

## Results record

| Check | Result | Date/device/commit |
|---|---|---|
| Convention build | Not recorded | |
| Money tests | Not recorded | |
| Currency registry tests | User previously reported passes; regression not recorded | |
| Accounts domain tests | Not recorded | |
| Android assembly | Not recorded | |
| DAO instrumentation | Not recorded | |
| Repository instrumentation | Not recorded | |
| Application graph tests | Not recorded | |
| Presentation module assembly after alias correction | Not recorded | |

Do not run git add . blindly if unrelated local work exists. Review the diff and
stage only intended checkpoint changes.
