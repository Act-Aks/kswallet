# KsWallet documentation

Snapshot: 2026-10-28, end-of-session handoff.

This pack reconstructs the current foundation implementation from the conversation,
user-supplied domain code, foundation UI code, and version catalog. It is not a
repository audit. Merge with existing documentation; do not overwrite differing
repository facts without checking them.

## Read in order

1. [Project status](project/status.md)
2. [Architecture](architecture/overview.md)
3. [Implementation roadmap](project/roadmap.md)
4. [Architecture decisions](architecture/decisions.md)
5. [Money and currency policy](architecture/money-and-currency.md)
6. [Accounts contracts](architecture/accounts.md)
7. [Dependency baseline](development/dependency-baseline.md)
8. [Verification checklist](development/verification.md)
9. [Tomorrow's handoff](handoffs/2026-10-28.md)

## Documentation rules

- Distinguish planned, supplied, reported implemented, and verified.
- A code example in a conversation is not proof it exists in the repository.
- Record actual command results before changing status to verified.
- Use the repository version catalog as the dependency source of truth.
- Superseded code samples are not alternative implementations to keep.
- Do not import older project iterations into this build without explicit review.
- Update the handoff, status, and decisions when a checkpoint changes.

The complete original product specification is not available in this conversation.
The roadmap describes the evidenced plan and explicitly separates unconfirmed scope.
