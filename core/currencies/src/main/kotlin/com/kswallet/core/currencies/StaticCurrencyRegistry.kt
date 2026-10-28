package com.kswallet.core.currencies

import com.kswallet.core.money.CurrencyCode
import com.kswallet.core.money.CurrencyRegistry
import com.kswallet.core.money.CurrencyUnit

class StaticCurrencyRegistry(
    currencies: Collection<CurrencyUnit>,
) : CurrencyRegistry {

    private val byCode: Map<CurrencyCode, CurrencyUnit>

    init {
        val snapshot = currencies.toList()

        require(snapshot.isNotEmpty()) {
            "Currency registry requires at least one currency."
        }

        val duplicateCodes = snapshot
            .groupBy { it.code }
            .filterValues { definitions ->
                definitions.size > 1
            }
            .keys

        require(duplicateCodes.isEmpty()) {
            "Duplicate currency codes: " +
                    duplicateCodes
                        .map { it.value }
                        .sorted()
                        .joinToString()
        }

        byCode = snapshot
            .sortedBy { it.code.value }
            .associateBy { it.code }
    }

    override fun find(code: CurrencyCode): CurrencyUnit? =
        byCode[code]

    override fun supportedCurrencies(): List<CurrencyUnit> =
        byCode.values.toList()
}