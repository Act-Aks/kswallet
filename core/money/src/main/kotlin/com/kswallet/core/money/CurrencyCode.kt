package com.kswallet.core.money

import java.util.Locale

@JvmInline
value class CurrencyCode private constructor(
    val value: String,
) {

    init {
        require(
            value.length == 3 && value.all { character -> character in 'A'..'Z' },
        ) {
            "Currency code must contain exactly three uppercase ASCII letters."
        }
    }

    override fun toString(): String = value

    companion object {

        /**
         * Normalizes surrounding whitespace and letter case.
         *
         * Validates syntax only, not membership in an official registry.
         */
        fun of(value: String): CurrencyCode = CurrencyCode(
            value = value.trim().uppercase(Locale.ROOT),
        )
    }
}