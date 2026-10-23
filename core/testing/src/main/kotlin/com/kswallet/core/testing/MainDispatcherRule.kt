package com.kswallet.core.testing

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

/**
 * Replaces `Dispatchers.Main` for the duration of a JVM unit test, which is what
 * `viewModelScope` in every MVI ViewModel needs.
 *
 * ```kotlin
 * @RegisterExtension
 * val mainDispatcher = MainDispatcherExtension(testDispatcher)
 * ```
 *
 * A JUnit Jupiter extension rather than a JUnit 4 rule, because that is what
 * the unit-test suite runs on (the Compose screen tests are the JUnit 4
 * holdouts - Compose ships no Jupiter test rule).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherExtension(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : BeforeEachCallback, AfterEachCallback {

    override fun beforeEach(context: ExtensionContext) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun afterEach(context: ExtensionContext) {
        Dispatchers.resetMain()
    }
}
