package com.kswallet.app.di

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.kswallet.core.database.WalletDatabase
import com.kswallet.core.database.account.AccountDao
import com.kswallet.core.money.CurrencyCode
import com.kswallet.core.money.CurrencyRegistry
import com.kswallet.feature.accounts.domain.model.AccountName
import com.kswallet.feature.accounts.domain.model.AccountNameResult
import com.kswallet.feature.accounts.domain.model.NewAccount
import com.kswallet.feature.accounts.domain.repository.AccountRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class PersistenceModuleTest {

    private lateinit var context: Context
    private lateinit var databaseName: String

    @Before
    fun setUp() {
        context = InstrumentationRegistry
            .getInstrumentation()
            .targetContext
            .applicationContext

        databaseName =
            "persistence-test-${UUID.randomUUID()}.db"
    }

    @After
    fun tearDown() {
        context.deleteDatabase(databaseName)
    }

    @Test
    fun graphResolvesSingletonDependencies() {
        val graph = newGraph()

        try {
            val koin = graph.koin

            val database = koin.get<WalletDatabase>()
            val dao = koin.get<AccountDao>()
            val registry = koin.get<CurrencyRegistry>()
            val repository = koin.get<AccountRepository>()

            assertSame(
                database,
                koin.get<WalletDatabase>(),
            )

            assertSame(
                dao,
                koin.get<AccountDao>(),
            )

            assertSame(
                registry,
                koin.get<CurrencyRegistry>(),
            )

            assertSame(
                repository,
                koin.get<AccountRepository>(),
            )
        } finally {
            graph.close()
        }
    }

    @Test
    fun accountSurvivesClosingAndReopeningGraph() = runBlocking {
        val firstGraph = newGraph()

        val created = try {
            val registry = firstGraph.koin.get<CurrencyRegistry>()

            val currency = requireNotNull(
                registry.find(CurrencyCode.of("INR")),
            )

            firstGraph.koin
                .get<AccountRepository>()
                .create(
                    NewAccount(
                        name = name("Persistence check"),
                        currency = currency,
                    ),
                )
        } finally {
            firstGraph.close()
        }

        val secondGraph = newGraph()

        try {
            val restored = secondGraph.koin
                .get<AccountRepository>()
                .findById(created.id)

            assertEquals(
                created,
                restored,
            )
        } finally {
            secondGraph.close()
        }
    }

    private fun newGraph(): KoinApplication =
        koinApplication {
            androidContext(context)

            modules(
                persistenceModule(
                    databaseName = databaseName,
                ),
            )
        }

    private fun name(value: String): AccountName =
        when (val result = AccountName.parse(value)) {
            is AccountNameResult.Valid -> result.name

            is AccountNameResult.Invalid -> {
                error("Invalid test name: ${result.reason}")
            }
        }
}