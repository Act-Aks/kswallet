# Architecture decisions

Decision register reconstructed on 2026-10-28. Verify implementation against Git.

| ID | Decision | State |
|---|---|---|
| ADR-001 | Modular layers and feature boundaries | Adopted direction |
| ADR-002 | Exact money values and explicit currency scales | Adopted direction |
| ADR-003 | Curated offline currency registry, separate from money values | Implemented; user reported passes |
| ADR-004 | Room 3 replaces the earlier Room 2 proposal | Adopted |
| ADR-005 | Bundled SQLite driver | Adopted |
| ADR-006 | Persist account currency code and scale | Adopted |
| ADR-007 | Single DAO in core:database; data module consumes it | Adopted |
| ADR-008 | Strict row mapping; no silent corruption repair | Adopted |
| ADR-009 | Update and read inside DAO transactions | Adopted |
| ADR-010 | Application-owned Koin composition | Supplied; verify merged startup |
| ADR-011 | Keep ScreenState/Action/Event foundation | Source supplied |
| ADR-012 | NeoDesign is independent of wallet business/storage | Adopted direction |

## ADR-004: Room 3, not Room 2

The first database proposal used Room 2.8.5. The user selected Room 3 before
continuing. The active baseline is Room 3.0.3.

Use androidx.room3 artifacts/imports and the androidx.room3 Gradle plugin.
Do not retain the superseded Room 2 code in project-owned database setup.
Unrelated libraries may still have their own Room 2 transitive dependencies.

## ADR-005: explicit database construction

The supplied Android-only implementation uses BundledSQLiteDriver and an explicit
factory for WalletDatabase_Impl. It localizes the experimental missing-constructor
opt-out on the database declaration rather than adding KMP expect declarations to
an ordinary Android module.

This is a version-sensitive implementation detail: confirm compilation under the
pinned Room release before changing its status to verified. The generated class
must not be hand-written or referenced by feature presentation.

No destructive fallback and no main-thread query allowance were proposed.

## ADR-006: creation policy differs from historical decoding

Creation checks registry membership and exact scale agreement. Reads reconstruct
CurrencyUnit from stored code/scale without current-registry membership checks.
This avoids silently reinterpreting stored metadata when support policy changes.

## ADR-007 and ADR-009: DAO ownership

There is exactly one AccountDao, under core:database. The later data-layer step
updates that DAO; it does not create another DAO in the feature.

It becomes an abstract class to contain generated query methods and concrete
transaction wrappers: renameAndRead and setArchivedAndRead.

A DAO-only change does not itself change the table schema version.

## ADR-008: failure boundaries

The domain repository returns values/null, not a typed result hierarchy.
Keep that contract. Null means a missing ID. Do not convert database failures,
malformed records, or cancellation into null/empty lists.

The proposed mapper throws MalformedAccountRecordException; invalid creation
currency throws AccountCurrencyRejectedException. UI mapping of these errors is
not yet implemented. Do not display raw exception messages to users.

Mapping the returned row occurs after the DAO transaction. A mapping failure is
not a promise to roll back an already committed update.

## ADR-011: demo versus product

FoundationState/Action/Event, FoundationCounterPolicy, FoundationViewModel,
FoundationRoute, and FoundationScreen demonstrate the architecture.
They are not completed wallet feature screens.

## Dependency correction

Use libs.androidx.lifecycle.viewmodel, matching the supplied catalog.
Do not add .ktx to that accessor or invent another catalog alias to hide a mismatch.

## Review needed later

- Error presentation policy and logging without leaking sensitive account data.
- Cancellation and contention tests.
- Historical schema migrations and malformed-field test coverage.
- Product navigation design (not established for this foundation build).
- Accessibility and large-list behavior of the first real screen.
