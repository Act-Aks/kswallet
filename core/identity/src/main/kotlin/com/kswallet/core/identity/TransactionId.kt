package com.kswallet.core.identity

import java.util.UUID

/**
 * Strongly typed identifier for a transaction.
 *
 * A transaction identifier is independent of an [AccountId]. A transaction
 * references an account through its `accountId` property, but the two
 * identifiers must never be interchangeable.
 *
 * @property value the underlying transaction UUID.
 */
@JvmInline
value class TransactionId(
    override val value: UUID,
) : UuidIdentity {

    /**
     * Returns the canonical lowercase UUID representation of this transaction ID.
     */
    override fun toString(): String = value.toString()

    companion object {

        /**
         * Creates a new randomly generated [TransactionId].
         *
         * @return a new transaction identifier.
         */
        fun new(): TransactionId = TransactionId(
            value = newIdentityUuid(),
        )

        /**
         * Parses [value] into a [TransactionId].
         *
         * @param value the canonical UUID representation of a transaction ID.
         * @return the parsed transaction identifier.
         * @throws IllegalArgumentException if [value] is not a valid UUID or
         * is not in canonical form.
         */
        fun parse(value: String): TransactionId = TransactionId(
            value = parseIdentityUuid(value),
        )
    }
}