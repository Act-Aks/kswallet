package com.kswallet.core.database.account

import androidx.sqlite.SQLiteException
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kswallet.core.database.WalletDatabase
import com.kswallet.core.database.WalletDatabaseFactory
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.milliseconds

@RunWith(AndroidJUnit4::class)
class AccountDaoTest {

    private lateinit var database: WalletDatabase
    private lateinit var dao: AccountDao

    @Before
    fun setUp() {
        database = WalletDatabaseFactory.createInMemory()
        dao = database.accountDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndReadPreservesAllFields() = runBlocking {
        val expected = account(
            id = "account-1",
            name = "Travel",
            currencyCode = "KWD",
            currencyScale = 3,
            archived = true,
        )

        dao.insert(expected)

        assertEquals(
            expected,
            dao.findById(expected.id),
        )
    }

    @Test
    fun missingAccountReturnsNull() = runBlocking {
        assertNull(
            dao.findById("missing"),
        )
    }

    @Test
    fun duplicateIdFailsWithoutReplacingOriginal() = runBlocking {
        val original = account(
            id = "account-1",
            name = "Original",
        )

        dao.insert(original)

        val result = runCatching {
            dao.insert(
                original.copy(name = "Replacement"),
            )
        }

        assertTrue(
            "Expected a SQLite constraint failure",
            result.exceptionOrNull() is SQLiteException,
        )

        assertEquals(
            original,
            dao.findById(original.id),
        )
    }

    @Test
    fun renameChangesOnlyName() = runBlocking {
        val original = account(
            id = "account-1",
            name = "Original",
            currencyCode = "JPY",
            currencyScale = 0,
        )

        dao.insert(original)

        val updatedRows = dao.rename(
            id = original.id,
            name = "Renamed",
        )

        assertEquals(1, updatedRows)

        assertEquals(
            original.copy(name = "Renamed"),
            dao.findById(original.id),
        )
    }

    @Test
    fun archivingChangesOnlyArchiveFlag() = runBlocking {
        val original = account(
            id = "account-1",
            name = "Travel",
        )

        dao.insert(original)

        assertEquals(
            1,
            dao.setArchived(original.id, true),
        )

        assertEquals(
            original.copy(archived = true),
            dao.findById(original.id),
        )

        assertEquals(
            1,
            dao.setArchived(original.id, false),
        )

        assertEquals(
            original,
            dao.findById(original.id),
        )
    }

    @Test
    fun updatesForMissingAccountAffectNoRows() = runBlocking {
        assertEquals(
            0,
            dao.rename("missing", "New name"),
        )

        assertEquals(
            0,
            dao.setArchived("missing", true),
        )
    }

    @Test
    fun observationUsesDeterministicOrdering() = runBlocking {
        dao.insert(
            account(id = "c", name = "Beta"),
        )
        dao.insert(
            account(id = "b", name = "alpha"),
        )
        dao.insert(
            account(id = "a", name = "Alpha"),
        )

        val result = withTimeout(10_000.milliseconds) {
            dao.observeAll().first()
        }

        assertEquals(
            listOf("a", "b", "c"),
            result.map { it.id },
        )
    }

    @Test
    fun activeObservationReceivesInsertedAccount() = runBlocking {
        withTimeout(10_000.milliseconds) {
            val initialEmissionReceived = CompletableDeferred<Unit>()

            val observed = async {
                dao.observeAll()
                    .onEach {
                        initialEmissionReceived.complete(Unit)
                    }
                    .first { accounts ->
                        accounts.any { it.id == "account-1" }
                    }
            }

            initialEmissionReceived.await()

            val expected = account(
                id = "account-1",
                name = "Travel",
            )

            dao.insert(expected)

            assertEquals(
                listOf(expected),
                observed.await(),
            )
        }
    }

    private fun account(
        id: String,
        name: String,
        currencyCode: String = "INR",
        currencyScale: Int = 2,
        archived: Boolean = false,
    ): AccountEntity =
        AccountEntity(
            id = id,
            name = name,
            currencyCode = currencyCode,
            currencyScale = currencyScale,
            archived = archived,
        )
}