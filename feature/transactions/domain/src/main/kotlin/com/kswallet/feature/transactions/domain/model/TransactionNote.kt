package com.kswallet.feature.transactions.domain.model

@JvmInline
value class TransactionNote private constructor(
    val value: String,
) {

    companion object {

        const val MAX_LENGTH = 500

        fun of(value: String?): TransactionNote? {
            val normalized = value?.trim().orEmpty()

            if (normalized.isEmpty()) {
                return null
            }

            require(normalized.length <= MAX_LENGTH) {
                "Transaction note must not exceed $MAX_LENGTH characters."
            }

            return TransactionNote(normalized)
        }
    }
}