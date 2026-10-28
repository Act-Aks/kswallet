package com.neodesign

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import com.neodesign.component.NeoNavigationBar
import com.neodesign.component.NeoNavigationItem
import com.neodesign.component.NeoText
import com.neodesign.theme.NeoTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class NeoNavigationBarTest(
    private val darkTheme: Boolean,
) {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun selectedDestinationExposesTabRole() {
        setNeoContent {
            NeoNavigationBar(
                items = items(),
                selectedKey = "home",
                onSelectionChange = {},
                modifier = Modifier.fillMaxWidth(),
            )
        }

        composeRule
            .onNodeWithText("Home")
            .assertIsSelected()
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.Role,
                    Role.Tab,
                ),
            )

        composeRule
            .onNodeWithText("History")
            .assertIsNotSelected()
    }

    @Test
    fun selectionUpdatesThroughCallerState() {
        setNeoContent {
            var selected by remember {
                mutableStateOf("home")
            }

            NeoNavigationBar(
                items = items(),
                selectedKey = selected,
                onSelectionChange = { selected = it },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        composeRule
            .onNodeWithText("History")
            .performClick()

        composeRule
            .onNodeWithText("History")
            .assertIsSelected()

        composeRule
            .onNodeWithText("Home")
            .assertIsNotSelected()
    }

    @Test
    fun reselectingCurrentDestinationDoesNotEmit() {
        var changes = 0

        setNeoContent {
            NeoNavigationBar(
                items = items(),
                selectedKey = "home",
                onSelectionChange = { changes++ },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        composeRule
            .onNodeWithText("Home")
            .performClick()

        composeRule.runOnIdle {
            assertEquals(0, changes)
        }
    }

    @Test
    fun disabledDestinationBlocksTouch() {
        var changes = 0

        setNeoContent {
            NeoNavigationBar(
                items = items(settingsEnabled = false),
                selectedKey = "home",
                onSelectionChange = { changes++ },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        composeRule
            .onNodeWithText("Settings")
            .assertIsNotEnabled()
            .performTouchInput {
                click()
            }

        composeRule.runOnIdle {
            assertEquals(0, changes)
        }
    }

    @Test
    fun disabledBarBlocksTouch() {
        var changes = 0

        setNeoContent {
            NeoNavigationBar(
                items = items(),
                selectedKey = "home",
                onSelectionChange = { changes++ },
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        composeRule
            .onNodeWithText("History")
            .assertIsNotEnabled()
            .performTouchInput {
                click()
            }

        composeRule.runOnIdle {
            assertEquals(0, changes)
        }
    }

    private fun items(
        settingsEnabled: Boolean = true,
    ): List<NeoNavigationItem<String>> = listOf(
        NeoNavigationItem(
            key = "home",
            label = "Home",
            icon = { _, tint ->
                NeoText("H", color = tint)
            },
        ),
        NeoNavigationItem(
            key = "history",
            label = "History",
            icon = { _, tint ->
                NeoText("T", color = tint)
            },
        ),
        NeoNavigationItem(
            key = "settings",
            label = "Settings",
            enabled = settingsEnabled,
            icon = { _, tint ->
                NeoText("S", color = tint)
            },
        ),
    )

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