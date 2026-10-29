# Money and currency policy

## Current intent

core:money owns money/currency values and parsing. core:currencies owns application
support policy. Exact internal implementation must be checked in source before
extending it; this document does not invent unavailable money APIs.

The parser examples in this session use integer minor units and an explicit
CurrencyUnit. Do not introduce Float/Double arithmetic for stored money amounts.

## Initial supported subset

| Code | Stored minor-unit scale |
|---|---:|
| EUR | 2 |
| INR | 2 |
| JPY | 0 |
| KWD | 3 |
| USD | 2 |

This is an application subset, not a complete currency directory.
Do not equate a minor-unit scale with cash-rounding rules or exchange rates.

## Registry behavior

- Offline and explicit.
- No fallback currency.
- No inference from device locale.
- Unknown code returns null.
- Duplicate definitions fail during construction, including identical duplicates.
- Supported output is sorted by currency code.
- Source collection mutation must not modify registry contents.

## Creation and decoding

- NewAccount contains a CurrencyUnit; registry membership alone is insufficient.
- Creation also checks the supplied scale against the registered scale.
- Existing rows use stored code and scale.
- Currency retirement must not make historical rows unreadable.
- Do not silently replace a persisted scale using a current registry definition.

## Update policy

Check currency amendments against SIX-maintained ISO 4217 data when changing the
support set. Update the dataset, tests, and documentation together.

Not implemented here: exchange rates, historical currency metadata versions,
cryptocurrency assets, localized currency names/symbols, or cash rounding.

If docs/architecture/currency-policy.md already exists, keep it as the detailed
currency reference and merge these notes rather than maintain conflicting tables.
