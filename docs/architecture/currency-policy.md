# Currency support policy

## Current scope

KsWallet initially supports a curated fiat-currency subset:

| Code | Minor-unit scale |
|------|------------------|
| EUR  | 2                |
| INR  | 2                |
| JPY  | 0                |
| KWD  | 3                |
| USD  | 2                |

This is not a complete ISO 4217 registry.

## Source policy

Currency metadata changes must be checked against the current or historical ISO 4217 lists published
by SIX, the maintenance agency.

Do not infer stored precision from:

- device locale;
- currency symbols;
- display formatting;
- a universal two-decimal assumption.

## Updates

- Review currency additions and amendments explicitly.
- Update dataset tests together with the dataset.
- Do not silently reinterpret existing stored amounts when a definition changes.
- Retiring support for new accounts must not make historical data unreadable.

## Not yet implemented

- Historical currency definitions.
- Separate availability for new accounts versus historical records.
- Localized names and symbols.
- Exchange rates.
- Cryptocurrency support.