package com.neodesign

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.test.platform.app.InstrumentationRegistry
import com.neodesign.component.NeoSecureTextField
import com.neodesign.theme.NeoTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class NeoSecureTextFieldTest(
    private val darkTheme: Boolean,
) {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun inputUpdatesCallerState() {
        val state = TextFieldState()
        val sample = "Sample-123"

        setNeoContent {
            NeoSecureTextField(
                state = state,
                label = "Password",
                passwordVisible = false,
                onPasswordVisibilityChange = {},
            )
        }

        composeRule
            .onNodeWithContentDescription("Password")
            .performTextInput(sample)

        composeRule.runOnIdle {
            assertTrue(state.text.toString() == sample)
        }
    }

    @Test
    fun concealedEditorExposesPasswordSemantics() {
        val state = TextFieldState()

        setNeoContent {
            NeoSecureTextField(
                state = state,
                label = "Password",
                passwordVisible = false,
                onPasswordVisibilityChange = {},
            )
        }

        composeRule
            .onNodeWithContentDescription("Password")
            .assert(
                SemanticsMatcher.keyIsDefined(
                    SemanticsProperties.Password,
                ),
            )
    }

    @Test
    fun visibilityToggleUpdatesCallerStateAndActionLabel() {
        val state = TextFieldState()

        setNeoContent {
            var visible by remember {
                mutableStateOf(false)
            }

            NeoSecureTextField(
                state = state,
                label = "Password",
                passwordVisible = visible,
                onPasswordVisibilityChange = {
                    visible = it
                },
            )
        }

        composeRule
            .onNodeWithContentDescription(showPasswordLabel())
            .performClick()

        composeRule
            .onNodeWithContentDescription(hidePasswordLabel())
            .assertIsDisplayed()
            .performClick()

        composeRule
            .onNodeWithContentDescription(showPasswordLabel())
            .assertIsDisplayed()
    }

    @Test
    fun disabledFieldAndToggleAreUnavailable() {
        val state = TextFieldState()
        var visibilityChanges = 0

        setNeoContent {
            NeoSecureTextField(
                state = state,
                label = "Password",
                passwordVisible = false,
                onPasswordVisibilityChange = {
                    visibilityChanges++
                },
                enabled = false,
            )
        }

        composeRule
            .onNodeWithContentDescription("Password")
            .assertIsNotEnabled()

        composeRule
            .onNodeWithContentDescription(showPasswordLabel())
            .assertIsNotEnabled()
            .performTouchInput {
                click()
            }

        composeRule.runOnIdle {
            assertEquals(0, visibilityChanges)
        }
    }

    @Test
    fun errorIsVisibleAndExposedOnEditor() {
        val state = TextFieldState()

        setNeoContent {
            NeoSecureTextField(
                state = state,
                label = "Password",
                passwordVisible = false,
                onPasswordVisibilityChange = {},
                errorMessage = "Review this value.",
            )
        }

        composeRule
            .onNodeWithContentDescription("Password")
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.Error,
                    "Review this value.",
                ),
            )

        composeRule
            .onNodeWithText("Review this value.")
            .assertIsDisplayed()
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

    private fun showPasswordLabel(): String =
        InstrumentationRegistry
            .getInstrumentation()
            .targetContext
            .getString(R.string.neo_show_password)

    private fun hidePasswordLabel(): String =
        InstrumentationRegistry
            .getInstrumentation()
            .targetContext
            .getString(R.string.neo_hide_password)

    companion object {

        @JvmStatic
        @Parameterized.Parameters(name = "darkTheme={0}")
        fun themes(): Collection<Array<Any>> = listOf(
            arrayOf<Any>(false),
            arrayOf<Any>(true),
        )
    }
}