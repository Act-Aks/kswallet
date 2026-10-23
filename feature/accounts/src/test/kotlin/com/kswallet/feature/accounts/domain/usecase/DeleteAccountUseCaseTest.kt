package com.kswallet.feature.accounts.domain.usecase

import com.kswallet.core.common.result.DomainError
import com.kswallet.core.common.result.Outcome
import com.kswallet.core.model.AccountId
import com.kswallet.feature.accounts.domain.repository.AccountRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Deleting an account is the one destructive operation in the app, so its guard rail
 * ("N transactions will be deleted too") is tested directly.
 */
class DeleteAccountUseCaseTest {

    private val repository = mockk<AccountRepository>()
    private val useCase = DeleteAccountUseCase(repository)
    private val accountId = AccountId(1)

    @Test
    fun `deletes an empty account`() = runTest {
        coEvery { repository.countTransactions(accountId) } returns Outcome.Success(0)
        coEvery { repository.deleteAccount(accountId) } returns Outcome.Success(Unit)

        assertEquals(Outcome.Success(Unit), useCase(accountId))
        coVerify { repository.deleteAccount(accountId) }
    }

    @Test
    fun `refuses to delete an account that still has transactions`() = runTest {
        coEvery { repository.countTransactions(accountId) } returns Outcome.Success(3)

        val result = useCase(accountId)

        assertTrue(result is Outcome.Failure)
        assertTrue((result as Outcome.Failure).error is DomainError.Conflict)
        coVerify(exactly = 0) { repository.deleteAccount(any()) }
    }

    @Test
    fun `forces the delete when the user confirmed the data loss`() = runTest {
        coEvery { repository.countTransactions(accountId) } returns Outcome.Success(3)
        coEvery { repository.deleteAccount(accountId) } returns Outcome.Success(Unit)

        assertEquals(Outcome.Success(Unit), useCase(accountId, force = true))
        coVerify { repository.deleteAccount(accountId) }
    }

    @Test
    fun `does not delete when the usage check itself failed`() = runTest {
        coEvery { repository.countTransactions(accountId) } returns
            Outcome.Failure(DomainError.Data(IllegalStateException("query failed")))

        val result = useCase(accountId, force = true)

        assertTrue(result is Outcome.Failure)
        coVerify(exactly = 0) { repository.deleteAccount(any()) }
    }
}
