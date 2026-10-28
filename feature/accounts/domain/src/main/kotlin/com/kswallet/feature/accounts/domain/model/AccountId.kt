package com.kswallet.feature.accounts.domain.model

import java.util.UUID

@JvmInline
value class AccountId(
    val value: UUID,
) {

    override fun toString(): String = value.toString()

    companion object {

        fun new(): AccountId =
            AccountId(UUID.randomUUID())

        fun parse(value: String): AccountId {
            val parsed = UUID.fromString(value)

            require(parsed.toString().equals(value, ignoreCase = true)) {
                "Account ID must use the canonical UUID representation."
            }

            return AccountId(parsed)
        }
    }
}