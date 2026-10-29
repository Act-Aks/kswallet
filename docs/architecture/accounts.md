# Accounts contracts and persistence

## Domain source supplied by the user

Account contains AccountId, AccountName, CurrencyUnit, AccountStatus.
AccountStatus is Active or Archived; isArchived is derived.

AccountId wraps UUID, supports new(), and rejects noncanonical UUID text through
parse(). The domain parser permits uppercase canonical text. Storage mapping is
stricter: writes/reads use lowercase UUID strings consistently.

AccountName trims user input, rejects empty/too-long/control-character input, and
limits length to 80. AccountNameResult is Valid or Invalid.

NewAccount contains AccountName and CurrencyUnit.

NewAccountValidator reports NewAccountValidation.Valid/Invalid, with
NewAccountError.InvalidName, CurrencyRequired, or UnsupportedCurrency.

## Repository contract

```kotlin
fun observeAccounts(): Flow<List<Account>>
suspend fun findById(id: AccountId): Account?
suspend fun create(account: NewAccount): Account
suspend fun rename(id: AccountId, name: AccountName): Account?
suspend fun setStatus(id: AccountId, status: AccountStatus): Account?
```

observeAccounts includes active and archived accounts with deterministic ordering.
create generates an active account with a new identity and validates currency.
rename/setStatus return the updated account or null if missing.

## Storage

Table accounts, schema version 1 at this checkpoint:

| Column | Representation |
|---|---|
| id | non-null text primary key |
| name | non-null text |
| currency_code | non-null text |
| currency_scale | non-null integer |
| archived | non-null integer-backed Boolean |

Names are not unique. No balance column is present.

## DAO operations

- observeAll
- findById
- insert with ABORT conflict behavior
- rename
- setArchived
- renameAndRead transaction
- setArchivedAndRead transaction

Ordering: name COLLATE NOCASE, then id. This is not full locale-aware sorting.

## Mapping policy

- Validate stored UUID, name, currency code, and scale through their domain APIs.
- Stored names/codes must already be normalized.
- Do not silently trim/uppercase malformed storage.
- Do not skip malformed rows from observed lists.
- Reconstruct currency from persisted metadata without registry lookup.

## Test inventory supplied in the implementation steps

8 DAO tests: round-trip, missing lookup, duplicate rejection, rename isolation,
archive/unarchive isolation, missing updates, ordering, live observation.

14 repository tests: creation, unique identities, unsupported currency, scale
mismatch, historical registry independence, stored scale preservation, rename,
status changes, missing operations, archived ordering, live observation, malformed
name, malformed ID, malformed code.

2 app graph tests: singleton resolution and database close/reopen persistence.

These are supplied test counts, not an independently confirmed passing count.
Additional repository work should include malformed scales/uppercase IDs,
cancellation, transaction contention, and migrations when relevant.
