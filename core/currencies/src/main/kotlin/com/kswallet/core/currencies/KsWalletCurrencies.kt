package com.kswallet.core.currencies

import com.kswallet.core.money.CurrencyCode
import com.kswallet.core.money.CurrencyRegistry
import com.kswallet.core.money.CurrencyUnit

/**
 * Initial KsWallet-supported fiat currency subset.
 *
 * This is application support policy, not a complete ISO 4217 registry.
 */
fun createKsWalletCurrencyRegistry(): CurrencyRegistry =
    StaticCurrencyRegistry(
        currencies = listOf(
            currency(code = "EUR", scale = 2),
            currency(code = "INR", scale = 2),
            currency(code = "JPY", scale = 0),
            currency(code = "KWD", scale = 3),
            currency(code = "USD", scale = 2),
        ),
    )

private fun currency(
    code: String,
    scale: Int,
): CurrencyUnit =
    CurrencyUnit(
        code = CurrencyCode.of(code),
        minorUnitScale = scale,
    )