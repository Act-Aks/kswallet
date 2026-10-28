package com.kswallet.core.money

interface CurrencyRegistry {

    /**
     * Returns the supported definition for this code, or null if unsupported.
     */
    fun find(code: CurrencyCode): CurrencyUnit?

    /**
     * Returns a snapshot of supported currency definitions.
     *
     * Each currency code must occur only once.
     */
    fun supportedCurrencies(): List<CurrencyUnit>
}