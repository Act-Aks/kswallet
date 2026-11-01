package com.kswallet.core.identity

import java.util.UUID

/**
 * Represents an identifier whose underlying value is a [UUID].
 *
 * This interface allows strongly typed identifiers such as [AccountId] and
 * [TransactionId] to share a common shape without forcing them into the same
 * type. It is intentionally an interface because Kotlin `value class` types
 * can implement interfaces but cannot inherit from classes.
 *
 * Implementations should:
 *
 * - expose the underlying [UUID] through [value];
 * - render the identifier using the canonical UUID representation;
 * - provide their own `new()` and `parse()` companion-object functions;
 * - avoid accepting or producing non-canonical UUID text.
 *
 * @property value the underlying UUID value.
 */
interface UuidIdentity {

    val value: UUID

    override fun toString(): String
}

/**
 * Creates a new random [UUID] for a strongly typed identity.
 *
 * This function centralizes identity generation so every identifier uses the
 * same source of randomness and the same creation behavior.
 *
 * @return a newly generated, cryptographically random UUID.
 */
fun newIdentityUuid(): UUID = UUID.randomUUID()

/**
 * Parses [value] as a UUID and requires its canonical representation.
 *
 * Java's UUID parser accepts several textual formats, including uppercase
 * letters and UUIDs enclosed in braces. KsWallet, however, persists and
 * exchanges UUIDs only in the canonical lowercase representation produced by
 * [UUID.toString].
 *
 * Example of a canonical UUID:
 *
 * ```text
 * 123e4567-e89b-12d3-a456-426614174000
 * ```
 *
 * @param value the UUID text to parse.
 * @return the parsed UUID.
 * @throws IllegalArgumentException if [value] is not a valid UUID or is not
 * already in its canonical representation.
 */
fun parseIdentityUuid(value: String): UUID {
    val parsed = try {
        UUID.fromString(value)
    } catch (cause: IllegalArgumentException) {
        throw IllegalArgumentException(
            "Identity must be a valid UUID.",
            cause,
        )
    }

    require(parsed.toString() == value) {
        "Identity must use the canonical UUID representation."
    }

    return parsed
}