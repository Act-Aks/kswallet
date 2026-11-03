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

        /**
         * Reconstructs a timestamp previously persisted by the app.
         *
         * Unlike [of], this does not reject timestamps that are in the future
         * relative to the current device clock. This prevents stored data from
         * becoming unreadable if the device clock changes.
         */
        fun fromStored(value: Instant): TransactionTimestamp =
            TransactionTimestamp(value)
    }
}