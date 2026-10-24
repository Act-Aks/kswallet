package com.kswallet.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

private data object TestHome : KsWalletRoute
private data object TestEditor : KsWalletRoute

class KsWalletNavigationTest {
    private fun backStack(): NavBackStack<NavKey> = NavBackStack(TestHome)

    @Test
    fun `navigate pushes a key and identifies the active destination`() {
        val stack = backStack()

        stack.navigateTo(TestEditor)

        assertEquals(2, stack.size)
        assertTrue(stack.isOnDestination(TestEditor))
        assertFalse(stack.isOnDestination(TestHome))
    }

    @Test
    fun `pop returns to the previous key and protects the tab root`() {
        val stack = backStack()
        stack.navigateTo(TestEditor)

        assertTrue(stack.popBackStack())
        assertTrue(stack.isOnDestination(TestHome))
        assertFalse(stack.popBackStack())
        assertEquals(listOf(TestHome), stack)
    }

    @Test
    fun `repeating the active key does not duplicate the stack entry`() {
        val stack = backStack()
        stack.navigateTo(TestEditor)
        stack.navigateTo(TestEditor)

        assertEquals(listOf(TestHome, TestEditor), stack)
    }
}
