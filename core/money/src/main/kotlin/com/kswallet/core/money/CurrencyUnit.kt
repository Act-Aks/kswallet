package com.kswallet.core.money

data class CurrencyUnit(
    val code: CurrencyCode,
    val minorUnitScale: Int,
) {

    init {
        require(minorUnitScale in 0..9) {
            "Currency minor-unit scale must be between 0 and 9."
        }
    }

    val minorUnitsPerMajorUnit: Long
        get() {
            var factor = 1L

            repeat(minorUnitScale) {
                factor *= 10L
            }

            return factor
        }
}