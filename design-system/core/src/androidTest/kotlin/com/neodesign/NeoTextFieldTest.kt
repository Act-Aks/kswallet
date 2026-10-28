package com.neodesign

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.text.input.ImeAction
import com.neodesign.component.NeoTextField
import com.neodesign.theme.NeoTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class NeoTextFieldTest(
    private val darkTheme: Boolean,
) {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun typingUpdatesCallerOwnedState() {
        val state = TextFieldState()

        setNeoContent {
            NeoTextField(
                state = state,
                label = "Display name",
            )
        }

        val field = composeRule.onNodeWithContentDescription(
            "Display name",
        )

        field.performTextInput("Akash")
        field.assertTextEquals("Akash")

        composeRule.runOnIdle {
            assertEquals("Akash", state.text.toString())
        }
    }

    @Test
    fun replacementUpdatesCallerOwnedState() {
        val state = TextFieldState(
            initialText = "Original",
        )

        setNeoContent {
            NeoTextField(
                state = state,
                label = "Display name",
            )
        }

        val field = composeRule.onNodeWithContentDescription(
            "Display name",
        )

        field.performTextReplacement("Updated")
        field.assertTextEquals("Updated")

        composeRule.runOnIdle {
            assertEquals("Updated", state.text.toString())
        }
    }

    @Test
    fun errorIsVisibleAndExposedOnEditor() {
        setNeoContent {
            NeoTextField(
                state = TextFieldState(),
                label = "Display name",
                errorMessage = "Name is required.",
            )
        }

        composeRule
            .onNodeWithContentDescription("Display name")
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.Error,
                    "Name is required.",
                ),
            )

        composeRule
            .onNodeWithText("Name is required.")
            .assertIsDisplayed()
    }

    @Test
    fun disabledFieldExposesDisabledState() {
        val state = TextFieldState(
            initialText = "Unavailable",
        )

        setNeoContent {
            NeoTextField(
                state = state,
                label = "Disabled input",
                enabled = false,
            )
        }

        composeRule
            .onNodeWithContentDescription("Disabled input")
            .assertIsNotEnabled()
            .assertTextEquals("Unavailable")
    }

    @Test
    fun readOnlyFieldCanReceiveFocus() {
        val state = TextFieldState(
            initialText = "Reference value",
        )

        setNeoContent {
            NeoTextField(
                state = state,
                label = "Reference",
                readOnly = true,
            )
        }

        composeRule
            .onNodeWithContentDescription("Reference")
            .performClick()
            .assertIsFocused()
            .assertTextEquals("Reference value")
    }

    @Test
    fun imeActionInvokesCallerCallback() {
        var submissions = 0
        val state = TextFieldState()

        setNeoContent {
            NeoTextField(
                state = state,
                label = "Search",
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search,
                ),
                onImeAction = { submissions++ },
            )
        }

        composeRule
            .onNodeWithContentDescription("Search")
            .performClick()
            .performImeAction()

        composeRule.runOnIdle {
            assertEquals(1, submissions)
        }
    }

    private fun setNeoContent(
        content: @Composable () -> Unit,
    ) {
        composeRule.setContent {
            NeoTheme(darkTheme = darkTheme) {
                content()
            }
        }
    }

    companion object {

        @JvmStatic
        @Parameterized.Parameters(name = "darkTheme={0}")
        fun themes(): Collection<Array<Any>> = listOf(
            arrayOf<Any>(false),
            arrayOf<Any>(true),
        )
    }
}