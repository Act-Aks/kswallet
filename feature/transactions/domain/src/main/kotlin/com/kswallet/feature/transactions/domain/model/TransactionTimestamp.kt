package com.kswallet.feature.transactions.domain.model

import java.time.Instant

@JvmInline
value class TransactionTimestamp(
    val value: Instant,
) {

    init {
        require(!value.isAfter(Instant.now().plusSeconds(60))) {
            "Transaction timestamp cannot be in the future."
        }
    }

    companion object {

        fun now(): TransactionTimestamp = TransactionTimestamp(
            value = Instant.now(),
        )

        fun of(value: Instant): TransactionTimestamp = TransactionTimestamp(
            value = value,
        )
    }
}