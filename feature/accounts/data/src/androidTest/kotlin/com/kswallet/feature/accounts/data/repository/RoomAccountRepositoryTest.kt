package com.kswallet.feature.accounts.data.repository

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.kswallet.core.database.WalletDatabase
import com.kswallet.core.database.WalletDatabaseFactory
import com.kswallet.core.database.account.AccountDao
import com.kswallet.core.database.account.AccountEntity
import com.kswallet.core.money.CurrencyCode
import com.kswallet.core.money.CurrencyRegistry
import com.kswallet.core.money.CurrencyUnit
import com.kswallet.feature.accounts.data.error.AccountCurrencyRejectedException
import com.kswallet.feature.accounts.data.error.MalformedAccountRecordException
import com.kswallet.feature.accounts.domain.model.AccountId
import com.kswallet.feature.accounts.domain.model.AccountName
import com.kswallet.feature.accounts.domain.model.AccountNameResult
import com.kswallet.feature.accounts.domain.model.AccountStatus
import com.kswallet.feature.accounts.domain.model.NewAccount
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

@RunWith(AndroidJUnit4::class)
class RoomAccountRepositoryTest {

    private lateinit var context: Context
    private lateinit var databaseName: String
    private lateinit var database: WalletDatabase
    private lateinit var dao: AccountDao
    private lateinit var repository: RoomAccountRepository

    private val inr = currency("INR", 2)

    @Before
    fun setUp() {
        context = InstrumentationRegistry
            .getInstrumentation()
            .targetContext

        databaseName = "accounts-test-${UUID.randomUUID()}.db"

        database = WalletDatabaseFactory.create(
            context = context,
            databaseName = databaseName,
        )

        dao = database.accountDao()

        repository = RoomAccountRepository(
            accountDao = dao,
            currencyRegistry = registryOf(inr),
        )
    }

    @After
    fun tearDown() {
        if (::database.isInitialized) {
            database.close()
        }

        if (::context.isInitialized && ::databaseName.isInitialized) {
            context.deleteDatabase(databaseName)
        }
    }

    @Test
    fun createPersistsAnActiveAccount() = runBlocking {
        val created = repository.create(
            NewAccount(
                name = name("Travel"),
                currency = inr,
            ),
        )

        assertEquals(name("Travel"), created.name)
        assertEquals(inr, created.currency)
        assertEquals(AccountStatus.Active, created.status)

        assertEquals(
            created,
            repository.findById(created.id),
        )

        val stored = requireNotNull(
            dao.findById(created.id.toString()),
        )

        assertEquals("INR", stored.currencyCode)
        assertEquals(2, stored.currencyScale)
        assertEquals(false, stored.archived)
    }

    @Test
    fun creationGeneratesDistinctIdentities() = runBlocking {
        val input = NewAccount(
            name = name("Wallet"),
            currency = inr,
        )

        val first = repository.create(input)
        val second = repository.create(input)

        assertNotEquals(first.id, second.id)
    }

    @Test
    fun unsupportedCurrencyIsRejectedWithoutInsertion() {
        val error = assertThrows(
            AccountCurrencyRejectedException::class.java,
        ) {
            runBlocking {
                repository.create(
                    NewAccount(
                        name = name("Travel"),
                        currency = currency("USD", 2),
                    ),
                )
            }
        }

        assertEquals(CurrencyCode.of("USD"), error.code)
        assertNull(error.registeredScale)

        runBlocking {
            assertEquals(
                emptyList<AccountEntity>(),
                dao.observeAll().first(),
            )
        }
    }

    @Test
    fun incorrectCurrencyScaleIsRejectedWithoutInsertion() {
        val error = assertThrows(
            AccountCurrencyRejectedException::class.java,
        ) {
            runBlocking {
                repository.create(
                    NewAccount(
                        name = name("Travel"),
                        currency = currency("INR", 3),
                    ),
                )
            }
        }

        assertEquals(3, error.suppliedScale)
        assertEquals(2, error.registeredScale)

        runBlocking {
            assertEquals(
                emptyList<AccountEntity>(),
                dao.observeAll().first(),
            )
        }
    }

    @Test
    fun existingAccountDoesNotRequireRegistryMembership() = runBlocking {
        val id = AccountId.new()

        dao.insert(
            storedAccount(
                id = id,
                currencyCode = "KWD",
                currencyScale = 3,
            ),
        )

        val read = requireNotNull(repository.findById(id))

        assertEquals(
            currency("KWD", 3),
            read.currency,
        )
    }

    @Test
    fun existingAccountKeepsStoredScale() = runBlocking {
        val id = AccountId.new()

        dao.insert(
            storedAccount(
                id = id,
                currencyCode = "INR",
                currencyScale = 3,
            ),
        )

        val read = requireNotNull(repository.findById(id))

        assertEquals(3, read.currency.minorUnitScale)
    }

    @Test
    fun renameReturnsUpdatedAccountAndPreservesOtherFields() = runBlocking {
        val created = repository.create(
            NewAccount(
                name = name("Original"),
                currency = inr,
            ),
        )

        val updated = repository.rename(
            id = created.id,
            name = name("Renamed"),
        )

        assertEquals(
            created.copy(name = name("Renamed")),
            updated,
        )

        assertEquals(
            updated,
            repository.findById(created.id),
        )
    }

    @Test
    fun statusCanBeArchivedAndRestored() = runBlocking {
        val created = repository.create(
            NewAccount(
                name = name("Travel"),
                currency = inr,
            ),
        )

        assertEquals(
            created.copy(status = AccountStatus.Archived),
            repository.setStatus(
                id = created.id,
                status = AccountStatus.Archived,
            ),
        )

        assertEquals(
            created,
            repository.setStatus(
                id = created.id,
                status = AccountStatus.Active,
            ),
        )

        assertEquals(
            created,
            repository.findById(created.id),
        )
    }

    @Test
    fun missingAccountOperationsReturnNull() = runBlocking {
        val missingId = AccountId.new()

        assertNull(
            repository.findById(missingId),
        )

        assertNull(
            repository.rename(missingId, name("Renamed")),
        )

        assertNull(
            repository.setStatus(
                missingId,
                AccountStatus.Archived,
            ),
        )
    }

    @Test
    fun observationIncludesArchivedAccountsInDeterministicOrder() =
        runBlocking {
            val firstId = AccountId.parse(
                "00000000-0000-0000-0000-000000000001",
            )
            val secondId = AccountId.parse(
                "00000000-0000-0000-0000-000000000002",
            )
            val thirdId = AccountId.parse(
                "00000000-0000-0000-0000-000000000003",
            )

            dao.insert(
                storedAccount(
                    id = thirdId,
                    name = "Beta",
                ),
            )
            dao.insert(
                storedAccount(
                    id = secondId,
                    name = "alpha",
                ),
            )
            dao.insert(
                storedAccount(
                    id = firstId,
                    name = "Alpha",
                    archived = true,
                ),
            )

            val observed = withTimeout(10_000.milliseconds) {
                repository.observeAccounts().first()
            }

            assertEquals(
                listOf(firstId, secondId, thirdId),
                observed.map { it.id },
            )

            assertEquals(
                AccountStatus.Archived,
                observed.first().status,
            )
        }

    @Test
    fun observationReceivesCreatedAccount() = runBlocking {
        withTimeout(10_000.milliseconds) {
            val initialEmissionReceived = CompletableDeferred<Unit>()

            val observed = async {
                repository.observeAccounts()
                    .onEach {
                        initialEmissionReceived.complete(Unit)
                    }
                    .first { it.isNotEmpty() }
            }

            initialEmissionReceived.await()

            val created = repository.create(
                NewAccount(
                    name = name("Travel"),
                    currency = inr,
                ),
            )

            assertEquals(
                listOf(created),
                observed.await(),
            )
        }
    }

    @Test
    fun malformedStoredNameIsNotSilentlyNormalized() {
        val id = AccountId.new()

        runBlocking {
            dao.insert(
                storedAccount(
                    id = id,
                    name = " Travel ",
                ),
            )
        }

        val error = assertThrows(
            MalformedAccountRecordException::class.java,
        ) {
            runBlocking {
                repository.findById(id)
            }
        }

        assertEquals(id.toString(), error.storedAccountId)
    }

    @Test
    fun malformedStoredIdFailsObservation() {
        runBlocking {
            dao.insert(
                storedAccount().copy(
                    id = "not-a-uuid",
                ),
            )
        }

        assertThrows(
            MalformedAccountRecordException::class.java,
        ) {
            runBlocking {
                withTimeout(10_000) {
                    repository.observeAccounts().first()
                }
            }
        }
    }

    @Test
    fun malformedStoredCurrencyCodeIsNotSilentlyNormalized() {
        val id = AccountId.new()

        runBlocking {
            dao.insert(
                storedAccount(
                    id = id,
                    currencyCode = "inr",
                ),
            )
        }

        assertThrows(
            MalformedAccountRecordException::class.java,
        ) {
            runBlocking {
                repository.findById(id)
            }
        }
    }

    private fun storedAccount(
        id: AccountId = AccountId.new(),
        name: String = "Travel",
        currencyCode: String = "INR",
        currencyScale: Int = 2,
        archived: Boolean = false,
    ): AccountEntity =
        AccountEntity(
            id = id.toString(),
            name = name,
            currencyCode = currencyCode,
            currencyScale = currencyScale,
            archived = archived,
        )

    private fun name(value: String): AccountName =
        when (val result = AccountName.parse(value)) {
            is AccountNameResult.Valid -> result.name

            is AccountNameResult.Invalid -> {
                error("Invalid test account name: ${result.reason}")
            }
        }

    private fun currency(
        code: String,
        scale: Int,
    ): CurrencyUnit =
        CurrencyUnit(
            code = CurrencyCode.of(code),
            minorUnitScale = scale,
        )

    private fun registryOf(
        vararg currencies: CurrencyUnit,
    ): CurrencyRegistry {
        val definitions = currencies.associateBy { it.code }

        return object : CurrencyRegistry {

            override fun find(code: CurrencyCode): CurrencyUnit? =
                definitions[code]

            override fun supportedCurrencies(): List<CurrencyUnit> =
                definitions.values.sortedBy { it.code.value }
        }
    }
}