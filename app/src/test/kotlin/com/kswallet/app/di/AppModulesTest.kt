package com.kswallet.app.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.kswallet.app.foundation.FoundationAction
import com.kswallet.app.foundation.FoundationCounterPolicy
import com.kswallet.app.foundation.FoundationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.verify.verify

@OptIn(ExperimentalCoroutinesApi::class)
class AppModulesTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var application: KoinApplication

    private val stores = mutableListOf<ViewModelStore>()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        application = koinApplication {
            modules(appModules)
        }
    }

    @AfterEach
    fun tearDown() {
        try {
            stores.forEach { store ->
                store.clear()
            }

            application.close()
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `application modules have required constructor definitions`() {
        val completeModule = module {
            includes(*appModules.toTypedArray())
        }

        completeModule.verify()
    }

    @Test
    fun `counter policy is a singleton within one container`() {
        val first = application.koin.get<FoundationCounterPolicy>()
        val second = application.koin.get<FoundationCounterPolicy>()

        assertSame(first, second)
    }

    @Test
    fun `ViewModel resolves with its constructor dependency`() {
        val provider = newViewModelProvider()

        val viewModel = provider[FoundationViewModel::class.java]

        val policy = application.koin.get<FoundationCounterPolicy>()

        assertEquals(0, viewModel.state.value.count)
        assertEquals(
            policy.maximum,
            viewModel.state.value.maximum,
        )

        viewModel.onAction(
            FoundationAction.IncrementClicked,
        )

        assertEquals(1, viewModel.state.value.count)
    }

    @Test
    fun `same ViewModelStore retains the ViewModel`() {
        val provider = newViewModelProvider()

        val first = provider[FoundationViewModel::class.java]

        first.onAction(
            FoundationAction.IncrementClicked,
        )

        val second = provider[FoundationViewModel::class.java]

        assertSame(first, second)
        assertEquals(1, second.state.value.count)
    }

    @Test
    fun `different ViewModelStores receive independent ViewModels`() {
        val firstProvider = newViewModelProvider()
        val secondProvider = newViewModelProvider()

        val first = firstProvider[FoundationViewModel::class.java]

        val second = secondProvider[FoundationViewModel::class.java]

        first.onAction(
            FoundationAction.IncrementClicked,
        )

        assertNotSame(first, second)
        assertEquals(1, first.state.value.count)
        assertEquals(0, second.state.value.count)
    }

    private fun newViewModelProvider(): ViewModelProvider {
        val store = ViewModelStore().also(stores::add)

        val factory = object : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(
                modelClass: Class<T>,
            ): T {
                require(
                    modelClass == FoundationViewModel::class.java,
                ) {
                    "Unsupported ViewModel: ${modelClass.name}"
                }

                return modelClass.cast(
                    application.koin.get<FoundationViewModel>(),
                )
            }
        }

        return ViewModelProvider(
            store,
            factory,
        )
    }
}