package com.kswallet.core.identity

import java.util.UUID

/**
 * Strongly typed identifier for an account.
 *
 * Using a dedicated type prevents an account identifier from being confused
 * with another UUID-based identifier, such as [TransactionId].
 *
 * The identifier wraps a [UUID] and is represented externally using its
 * canonical lowercase UUID form.
 *
 * @property value the underlying account UUID.
 */
@JvmInline
value class AccountId(
    override val value: UUID,
) : UuidIdentity {

    /**
     * Returns the canonical lowercase UUID representation of this account ID.
     */
    override fun toString(): String = value.toString()

    companion object {

        /**
         * Creates a new randomly generated [AccountId].
         *
         * @return a new account identifier.
         */
        fun new(): AccountId = AccountId(
            value = newIdentityUuid(),
        )

        /**
         * Parses [value] into an [AccountId].
         *
         * @param value the canonical UUID representation of an account ID.
         * @return the parsed account identifier.
         * @throws IllegalArgumentException if [value] is not a valid UUID or
         * is not in canonical form.
         */
        fun parse(value: String): AccountId = AccountId(
            value = parseIdentityUuid(value),
        )
    }
}