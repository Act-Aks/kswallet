package com.kswallet.feature.transactions.domain.usecase

import com.kswallet.core.common.result.fold
import com.kswallet.core.common.time.TimeProvider
import com.kswallet.core.model.Account
import com.kswallet.core.model.Category
import com.kswallet.core.model.Transaction
import com.kswallet.core.model.TransactionId
import com.kswallet.core.model.TransactionType
import com.kswallet.feature.transactions.domain.repository.TransactionRepository
import com.kswallet.feature.transactions.domain.validation.TransactionFormInput
import com.kswallet.feature.transactions.domain.validation.TransactionFormValidator

class SaveTransactionUseCase(
    private val repository: TransactionRepository,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke(
        input: TransactionFormInput,
        accounts: List<Account>,
        categories: List<Category>,
        existing: Transaction?,
    ): SaveTransactionResult {
        val errors = TransactionFormValidator.validate(input, accounts, categories)
        if (errors.isNotEmpty()) return SaveTransactionResult.Invalid(errors)

        val account = accounts.first { it.id == input.accountId }
        val amount = requireNotNull(TransactionFormValidator.parseAmount(input, account.currency))

        val transaction = Transaction(
            id = existing?.id ?: TransactionId.UNSAVED,
            type = input.type,
            amount = amount,
            accountId = account.id,
            transferAccountId = input.transferAccountId.takeIf { input.type == TransactionType.TRANSFER },
            categoryId = input.categoryId.takeIf { input.type != TransactionType.TRANSFER },
            dateTime = existing?.dateTime ?: timeProvider.now(),
            note = input.note.trim().takeIf { it.isNotEmpty() },
            createdAt = existing?.createdAt ?: timeProvider.now(),
        )

        return repository.upsertTransaction(transaction).fold(
            onSuccess = { SaveTransactionResult.Saved(it) },
            onFailure = { SaveTransactionResult.Failed(it) },
        )
    }
}

