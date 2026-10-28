package com.kswallet.feature.accounts.domain.model

@JvmInline
value class AccountName private constructor(
    val value: String,
) {

    init {
        require(value.isNotBlank())
        require(value == value.trim())
        require(value.length <= MAX_LENGTH)
        require(value.none { it.isISOControl() })
    }

    override fun toString(): String = value

    companion object {

        const val MAX_LENGTH: Int = 80

        fun parse(input: String): AccountNameResult {
            val normalized = input.trim()

            if (normalized.isEmpty()) {
                return AccountNameResult.Invalid(
                    AccountNameError.Empty,
                )
            }

            if (normalized.any { it.isISOControl() }) {
                return AccountNameResult.Invalid(
                    AccountNameError.ControlCharacter,
                )
            }

            if (normalized.length > MAX_LENGTH) {
                return AccountNameResult.Invalid(
                    AccountNameError.TooLong,
                )
            }

            return AccountNameResult.Valid(
                AccountName(normalized),
            )
        }
    }
}