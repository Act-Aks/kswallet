package com.kswallet.app.foundation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FoundationViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var store: ViewModelStore
    private lateinit var viewModel: FoundationViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        store = ViewModelStore()

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
            ): T {
                require(
                    modelClass.isAssignableFrom(
                        FoundationViewModel::class.java,
                    ),
                )

                @Suppress("UNCHECKED_CAST")
                return FoundationViewModel(
                    counterPolicy = FoundationCounterPolicy(),
                ) as T
            }
        }

        viewModel = ViewModelProvider(
            store,
            factory,
        )[FoundationViewModel::class.java]
    }

    @AfterEach
    fun tearDown() {
        store.clear()
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state starts at zero`() {
        assertEquals(0, viewModel.state.value.count)
        assertEquals(10, viewModel.state.value.maximum)
    }

    @Test
    fun `increment updates state`() {
        viewModel.onAction(
            FoundationAction.IncrementClicked,
        )

        assertEquals(1, viewModel.state.value.count)
    }

    @Test
    fun `increment does not exceed maximum`() {
        repeat(20) {
            viewModel.onAction(
                FoundationAction.IncrementClicked,
            )
        }

        assertEquals(10, viewModel.state.value.count)
        assertFalse(viewModel.state.value.canIncrement)
    }

    @Test
    fun `reset updates state and emits notification`() = runTest(
        dispatcher,
    ) {
        viewModel.onAction(
            FoundationAction.IncrementClicked,
        )

        val event = async {
            viewModel.events.first()
        }

        viewModel.onAction(
            FoundationAction.ResetClicked,
        )

        advanceUntilIdle()

        assertEquals(0, viewModel.state.value.count)
        assertEquals(
            FoundationEvent.ResetCompleted,
            event.await(),
        )
    }
}