# Implementation roadmap

This reconstructs the evidenced engineering plan, not an unavailable full original
product brief. No progress percentage is assigned because total product scope has
not been frozen.

## Established sequence

1. Build conventions, dependency catalog, shared screen foundation, NeoDesign.
2. Exact money/currency primitives and account domain.
3. Step 5B: curated supported-currency registry.
4. Step 5C.1: Room 3 account storage foundation.
5. Step 5C.2: strict mapping and accounts repository.
6. Step 5C.3: application DI and persistence verification.
7. Step 5D: first real accounts presentation slice (current focus).

Numbers before 5B are descriptive; exact original checkpoint numbering was not
available and is deliberately not invented.

## Next: read-only accounts presentation

- Inspect ScreenViewModel implementation.
- Confirm current presentation module build configuration.
- Define accounts state/action/event contract using core:screen.
- Implement loading, empty, content, error, and retry behavior.
- Observe through AccountRepository only.
- Define deliberate archived-account display behavior.
- Implement route and NeoDesign-based screen.
- Wire ViewModel in the existing application graph.
- Replace foundation entry only when the real route is ready.
- Test state transitions and observation/cancellation behavior.

## Following accounts slices

- Create-account form with NewAccountValidator.
- Currency selection using the curated registry.
- Prevent duplicate submissions and show user-facing errors.
- Rename interaction.
- Archive/unarchive interaction.
- Persistence-backed UI verification and accessibility review.

## Later engineering work

- Schema migrations when the schema changes.
- Stronger corruption, cancellation, and contention coverage.
- Process recreation verification.
- Product navigation once actual routes are established.
- Transaction-backed balances before displaying account balances.

## Product scope requiring confirmation

Transactions/ledger semantics, transfers, categories, budgets, reporting,
settings/onboarding, backup/restore, exchange rates, sync, authentication, and
security requirements are not all established in this foundation conversation.
Do not mark them implemented or promise an order based on older project memories.
Agree on scope before estimating the remainder of the complete product.

## Done means

A checkpoint is done when code exists, affected builds/tests pass, documentation
is updated, and the change is committed. Supplied snippets alone are not done.
