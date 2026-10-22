package com.kswallet.feature.transactions.domain.validation

import com.kswallet.core.model.Account
import com.kswallet.core.model.Category
import com.kswallet.core.model.CategoryType
import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import com.kswallet.core.model.TransactionType

/**
 * The invariants of a ledger entry, in one place.
 *
 * These rules are what keeps reports meaningful: a transfer between two accounts of
 * different currencies cannot be represented without an exchange rate, so it is rejected
 * instead of being silently rounded.
 */
object TransactionFormValidator {

    fun validate(
        input: TransactionFormInput,
        accounts: List<Account>,
        categories: List<Category>,
    ): List<TransactionFormError> = buildList {
        val source = accounts.firstOrNull { it.id == input.accountId }
        if (source == null) {
            add(TransactionFormError.AccountMissing)
            return@buildList
        }

        val amount = Money.parseOrNull(input.amountText, source.currency)
        if (input.amountText.isBlank() || amount == null) {
            add(TransactionFormError.InvalidAmount)
            return@buildList
        }
        if (!amount.isPositive) add(TransactionFormError.AmountNotPositive)

        when (input.type) {
            TransactionType.TRANSFER -> addAll(transferErrors(input, accounts, source))

            TransactionType.INCOME, TransactionType.EXPENSE -> {
                val category = categories.firstOrNull { it.id == input.categoryId }
                if (category == null || category.type != CategoryType.forTransactionType(input.type)) {
                    add(TransactionFormError.CategoryMissing)
                }
            }
        }
    }

    /**
     * A transfer needs a second account that exists, differs from the source and holds the
     * same currency - without an exchange rate there is no honest way to move money between
     * two currencies, so that case is rejected instead of rounded.
     */
    private fun transferErrors(
        input: TransactionFormInput,
        accounts: List<Account>,
        source: Account,
    ): List<TransactionFormError> {
        val target = accounts.firstOrNull { it.id == input.transferAccountId }
            ?: return listOf(TransactionFormError.TransferTargetMissing)

        return buildList {
            when {
                target.id == source.id -> add(TransactionFormError.TransferTargetSameAsSource)
                target.currency != source.currency -> add(TransactionFormError.CurrencyMismatch)
            }
        }
    }

    fun parseAmount(input: TransactionFormInput, currency: Currency): Money? =
        Money.parseOrNull(input.amountText, currency)
}
