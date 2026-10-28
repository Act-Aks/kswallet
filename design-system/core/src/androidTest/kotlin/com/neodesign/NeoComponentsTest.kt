package com.neodesign

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import com.neodesign.component.NeoButton
import com.neodesign.component.NeoCard
import com.neodesign.component.NeoIconButton
import com.neodesign.component.NeoSegmentOption
import com.neodesign.component.NeoSegmentedControl
import com.neodesign.component.NeoSegmentedControlLayout
import com.neodesign.component.NeoText
import com.neodesign.theme.NeoTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class NeoComponentsTest(
    private val darkTheme: Boolean,
) {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun button_exposesLabelAndButtonRole() {
        setNeoContent {
            NeoButton(
                text = "Save",
                onClick = {},
            )
        }

        composeRule
            .onNodeWithText("Save")
            .assertIsDisplayed()
            .assertIsEnabled()
            .assertHasClickAction()
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.Role,
                    Role.Button,
                ),
            )
    }

    @Test
    fun button_clickInvokesCallbackOnce() {
        var clicks = 0

        setNeoContent {
            NeoButton(
                text = "Save",
                onClick = { clicks++ },
            )
        }

        composeRule
            .onNodeWithText("Save")
            .performClick()

        composeRule.runOnIdle {
            assertEquals(1, clicks)
        }
    }

    @Test
    fun button_disabledBlocksTouch() {
        var clicks = 0

        setNeoContent {
            NeoButton(
                text = "Save",
                enabled = false,
                onClick = { clicks++ },
            )
        }

        composeRule
            .onNodeWithText("Save")
            .assertIsNotEnabled()
            .performTouchInput {
                click()
            }

        composeRule.runOnIdle {
            assertEquals(0, clicks)
        }
    }

    @Test
    fun button_loadingPreservesLabelAndBlocksTouch() {
        var clicks = 0

        setNeoContent {
            NeoButton(
                text = "Save changes",
                loading = true,
                onClick = { clicks++ },
            )
        }

        composeRule
            .onNodeWithText("Save changes")
            .assertIsNotEnabled()
            .assert(
                SemanticsMatcher.keyIsDefined(
                    SemanticsProperties.StateDescription,
                ),
            )
            .performTouchInput {
                click()
            }

        composeRule.runOnIdle {
            assertEquals(0, clicks)
        }
    }

    @Test
    fun iconButton_exposesDescriptionAndInvokesCallback() {
        var clicks = 0

        setNeoContent {
            NeoIconButton(
                contentDescription = "Add item",
                onClick = { clicks++ },
            ) {
                NeoText("+")
            }
        }

        composeRule
            .onNodeWithContentDescription("Add item")
            .assertIsEnabled()
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.Role,
                    Role.Button,
                ),
            )
            .performClick()

        composeRule.runOnIdle {
            assertEquals(1, clicks)
        }
    }

    @Test
    fun iconButton_disabledBlocksTouch() {
        var clicks = 0

        setNeoContent {
            NeoIconButton(
                contentDescription = "Add item",
                enabled = false,
                onClick = { clicks++ },
            ) {
                NeoText("+")
            }
        }

        composeRule
            .onNodeWithContentDescription("Add item")
            .assertIsNotEnabled()
            .performTouchInput {
                click()
            }

        composeRule.runOnIdle {
            assertEquals(0, clicks)
        }
    }

    @Test
    fun segmentedControl_changesSelectionThroughCallerState() {
        setNeoContent {
            var selected by remember {
                mutableStateOf("month")
            }

            NeoSegmentedControl(
                options = periodOptions(),
                selectedKey = selected,
                onSelectionChange = { selected = it },
                modifier = Modifier.fillMaxWidth(),
                layout = NeoSegmentedControlLayout.Horizontal,
            )
        }

        composeRule
            .onNodeWithText("Month")
            .assertIsSelected()

        composeRule
            .onNodeWithText("Week")
            .assertIsNotSelected()
            .performClick()

        composeRule
            .onNodeWithText("Week")
            .assertIsSelected()

        composeRule
            .onNodeWithText("Month")
            .assertIsNotSelected()
    }

    @Test
    fun segmentedControl_selectedOptionDoesNotEmitAgain() {
        var changes = 0

        setNeoContent {
            NeoSegmentedControl(
                options = periodOptions(),
                selectedKey = "month",
                onSelectionChange = { changes++ },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        composeRule
            .onNodeWithText("Month")
            .assertIsSelected()
            .performClick()

        composeRule.runOnIdle {
            assertEquals(0, changes)
        }
    }

    @Test
    fun segmentedControl_disabledOptionBlocksTouch() {
        var changes = 0

        setNeoContent {
            NeoSegmentedControl(
                options = listOf(
                    NeoSegmentOption(
                        key = "week",
                        label = "Week",
                    ),
                    NeoSegmentOption(
                        key = "month",
                        label = "Month",
                    ),
                    NeoSegmentOption(
                        key = "year",
                        label = "Year",
                        enabled = false,
                    ),
                ),
                selectedKey = "month",
                onSelectionChange = { changes++ },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        composeRule
            .onNodeWithText("Year")
            .assertIsNotEnabled()
            .performTouchInput {
                click()
            }

        composeRule
            .onNodeWithText("Month")
            .assertIsSelected()

        composeRule.runOnIdle {
            assertEquals(0, changes)
        }
    }

    @Test
    fun segmentedControl_disabledGroupBlocksTouch() {
        var changes = 0

        setNeoContent {
            NeoSegmentedControl(
                options = periodOptions(),
                selectedKey = "month",
                onSelectionChange = { changes++ },
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        composeRule
            .onNodeWithText("Week")
            .assertIsNotEnabled()
            .performTouchInput {
                click()
            }

        composeRule
            .onNodeWithText("Month")
            .assertIsSelected()

        composeRule.runOnIdle {
            assertEquals(0, changes)
        }
    }

    @Test
    fun passiveCard_doesNotExposeClickAction() {
        setNeoContent {
            NeoCard(
                modifier = Modifier.testTag("passive-card"),
            ) {
                NeoText("Information")
            }
        }

        composeRule
            .onNodeWithTag("passive-card")
            .assertIsDisplayed()
            .assertHasNoClickAction()

        composeRule
            .onNodeWithText("Information")
            .assertIsDisplayed()
    }

    @Test
    fun clickableCard_exposesButtonRoleAndInvokesCallback() {
        var clicks = 0

        setNeoContent {
            NeoCard(
                onClick = { clicks++ },
                onClickLabel = "Open details",
                modifier = Modifier.testTag("interactive-card"),
            ) {
                NeoText("Project details")
            }
        }

        composeRule
            .onNodeWithTag("interactive-card")
            .assertIsEnabled()
            .assertHasClickAction()
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.Role,
                    Role.Button,
                ),
            )
            .performClick()

        composeRule.runOnIdle {
            assertEquals(1, clicks)
        }
    }

    @Test
    fun clickableCard_disabledBlocksTouch() {
        var clicks = 0

        setNeoContent {
            NeoCard(
                onClick = { clicks++ },
                enabled = false,
                modifier = Modifier.testTag("disabled-card"),
            ) {
                NeoText("Unavailable")
            }
        }

        composeRule
            .onNodeWithTag("disabled-card")
            .assertIsNotEnabled()
            .performTouchInput {
                click()
            }

        composeRule.runOnIdle {
            assertEquals(0, clicks)
        }
    }

    private fun setNeoContent(
        content: @Composable () -> Unit,
    ) {
        composeRule.setContent {
            NeoTheme(darkTheme = darkTheme) {
                Column {
                    content()
                }
            }
        }
    }

    private fun periodOptions(): List<NeoSegmentOption<String>> =
        listOf(
            NeoSegmentOption(
                key = "week",
                label = "Week",
            ),
            NeoSegmentOption(
                key = "month",
                label = "Month",
            ),
            NeoSegmentOption(
                key = "year",
                label = "Year",
            ),
        )

    companion object {

        @JvmStatic
        @Parameterized.Parameters(name = "darkTheme={0}")
        fun themes(): Collection<Array<Any>> = listOf(
            arrayOf<Any>(false),
            arrayOf<Any>(true),
        )
    }
}