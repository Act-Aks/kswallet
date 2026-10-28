package com.kswallet.feature.accounts.domain.validation

import com.kswallet.core.money.CurrencyCode
import com.kswallet.core.money.CurrencyRegistry
import com.kswallet.core.money.CurrencyUnit
import com.kswallet.feature.accounts.domain.model.AccountNameError
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test

class NewAccountValidatorTest {

    private val supportedCurrency = CurrencyUnit(
        code = CurrencyCode.of("ABC"),
        minorUnitScale = 2,
    )

    private val registry = object : CurrencyRegistry {

        override fun find(code: CurrencyCode): CurrencyUnit? =
            supportedCurrency.takeIf {
                it.code == code
            }

        override fun supportedCurrencies(): List<CurrencyUnit> =
            listOf(supportedCurrency)
    }

    private val validator = NewAccountValidator(registry)

    @Test
    fun `valid draft uses normalized name and registry currency`() {
        val result = validator.validate(
            nameInput = "  Travel Fund  ",
            currencyCode = supportedCurrency.code,
        )

        val valid = assertInstanceOf(
            NewAccountValidation.Valid::class.java,
            result,
        )

        assertEquals(
            "Travel Fund",
            valid.account.name.value,
        )

        assertEquals(
            supportedCurrency,
            valid.account.currency,
        )
    }

    @Test
    fun `invalid name is reported`() {
        assertEquals(
            NewAccountValidation.Invalid(
                NewAccountError.InvalidName(
                    AccountNameError.Empty,
                ),
            ),
            validator.validate(
                nameInput = " ",
                currencyCode = supportedCurrency.code,
            ),
        )
    }

    @Test
    fun `missing currency is reported`() {
        assertEquals(
            NewAccountValidation.Invalid(
                NewAccountError.CurrencyRequired,
            ),
            validator.validate(
                nameInput = "Travel",
                currencyCode = null,
            ),
        )
    }

    @Test
    fun `unsupported currency is reported`() {
        val unsupported = CurrencyCode.of("XYZ")

        assertEquals(
            NewAccountValidation.Invalid(
                NewAccountError.UnsupportedCurrency(unsupported),
            ),
            validator.validate(
                nameInput = "Travel",
                currencyCode = unsupported,
            ),
        )
    }

    @Test
    fun `name failure takes precedence over missing currency`() {
        assertEquals(
            NewAccountValidation.Invalid(
                NewAccountError.InvalidName(
                    AccountNameError.Empty,
                ),
            ),
            validator.validate(
                nameInput = "",
                currencyCode = null,
            ),
        )
    }
}