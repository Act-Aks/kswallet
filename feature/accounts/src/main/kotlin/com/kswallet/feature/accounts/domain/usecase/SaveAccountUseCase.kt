package com.kswallet.feature.accounts.domain.usecase

import com.kswallet.core.common.result.DomainError
import com.kswallet.core.common.result.fold
import com.kswallet.core.common.time.TimeProvider
import com.kswallet.core.model.Account
import com.kswallet.core.model.AccountId
import com.kswallet.core.model.Money
import com.kswallet.feature.accounts.domain.repository.AccountRepository
import com.kswallet.feature.accounts.domain.validation.AccountFormError
import com.kswallet.feature.accounts.domain.validation.AccountFormInput
import com.kswallet.feature.accounts.domain.validation.AccountFormValidator

class SaveAccountUseCase(
    private val repository: AccountRepository,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke(input: AccountFormInput, existing: Account?): SaveAccountResult {
        val errors = AccountFormValidator.validate(input)
        if (errors.isNotEmpty()) return SaveAccountResult.Invalid(errors)

        val initialBalance = AccountFormValidator.parseInitialBalance(input)
            ?: Money.zero(input.currency)

        val account = Account(
            id = existing?.id ?: AccountId.UNSAVED,
            name = AccountFormValidator.normalizeName(input.name),
            type = input.type,
            currency = input.currency,
            // Balance is derived from the ledger; when creating an account it starts at the
            // initial balance, and any later change comes from transactions.
            balance = existing?.balance ?: initialBalance,
            initialBalance = initialBalance,
            iconKey = input.type.name.lowercase(),
            colorArgb = input.colorArgb,
            includedInTotal = input.includedInTotal,
            isArchived = existing?.isArchived ?: false,
            createdAt = existing?.createdAt ?: timeProvider.now(),
        )

        return repository.upsertAccount(account).fold(
            onSuccess = { SaveAccountResult.Saved(it) },
            onFailure = { SaveAccountResult.Failed(it) },
        )
    }
}

sealed interface SaveAccountResult {
    data class Saved(val id: AccountId) : SaveAccountResult
    data class Invalid(val errors: List<AccountFormError>) : SaveAccountResult
    data class Failed(val error: DomainError) : SaveAccountResult
}
