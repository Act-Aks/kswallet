package com.kswallet.feature.accounts.domain.usecase

import com.kswallet.core.common.result.DomainError
import com.kswallet.core.common.result.Outcome
import com.kswallet.core.model.AccountId
import com.kswallet.feature.accounts.domain.repository.AccountRepository

/**
 * Deleting an account is a data-loss operation. The use case refuses to do it while the
 * account still has transactions unless the caller explicitly forces it - which is why the
 * screen shows "N transactions will be deleted too" before asking again.
 */
class DeleteAccountUseCase(
    private val repository: AccountRepository,
) {
    suspend operator fun invoke(accountId: AccountId, force: Boolean = false): Outcome<Unit> {
        val count = repository.countTransactions(accountId)
        if (count is Outcome.Failure) return count

        val transactionCount = (count as Outcome.Success).value
        if (transactionCount > 0 && !force) {
            return Outcome.Failure(
                DomainError.Conflict("This account is used by $transactionCount transactions"),
            )
        }
        return repository.deleteAccount(accountId)
    }
}
